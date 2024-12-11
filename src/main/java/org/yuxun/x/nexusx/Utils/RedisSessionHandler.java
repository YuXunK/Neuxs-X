package org.yuxun.x.nexusx.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.yuxun.x.nexusx.Entity.User;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.yuxun.x.nexusx.Service.Impl.UserServiceImpl.logger;

@Component
public class RedisSessionHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper; // 用于 JSON 序列化和反序列化

    public RedisSessionHandler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 异步处理 Redis 操作（或者同步分离），保证一个用户在 Redis 中只存在一个 session
     * @author yuxun
     */
    public void handleSessionInRedisAsync(Long userId, User user) {
        String sessionKey = "user:session:" + userId; // Redis key
        try {
            // 将用户对象序列化为 JSON 字符串
            String userJson = objectMapper.writeValueAsString(user);

            CompletableFuture.runAsync(() -> {
                try {
                    // 检查用户是否已经存在会话
                    Boolean sessionExists = redisTemplate.opsForValue().setIfAbsent(sessionKey, userJson, 3, TimeUnit.HOURS);

                    if (sessionExists != null && sessionExists) {
                        // 如果会话不存在，直接存储新的用户信息
                        logger.info("为用户ID: {} 设置新的会话信息，过期时间为 3 小时", userId);
                    } else {
                        // 如果会话已存在，获取旧的用户信息
                        String oldUserJson = redisTemplate.opsForValue().get(sessionKey);
                        if (oldUserJson != null) {
                            User oldUser = objectMapper.readValue(oldUserJson, User.class);
                            logger.warn("用户ID: {} 已有会话，旧用户信息为: {}", userId, oldUser);
                        }

                        // 删除旧会话并存储新的用户信息
                        redisTemplate.delete(sessionKey);
                        redisTemplate.opsForValue().set(sessionKey, userJson, 3, TimeUnit.HOURS);
                        logger.info("用户ID: {} 的会话已更新为新的用户信息，过期时间为 3 小时", userId);
                    }
                } catch (Exception e) {
                    logger.error("Redis 存储会话信息失败，用户ID: {}", userId, e);
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("用户对象序列化失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 反序列化 Redis 中的用户数据
     * @author yuxun
     */
    public User getUserFromSession(String userId) {
        String sessionKey = "user:session:" + userId;
        String userJson = redisTemplate.opsForValue().get(sessionKey);

        if (userJson != null) {
            try {
                return objectMapper.readValue(userJson, User.class);
            } catch (JsonProcessingException e) {
                logger.error("用户会话反序列化失败，用户ID: {}", userId, e);
            }
        }
        return null;
    }
}
