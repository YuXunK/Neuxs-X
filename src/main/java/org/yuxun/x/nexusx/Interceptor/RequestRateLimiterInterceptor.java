package org.yuxun.x.nexusx.Interceptor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
public class RequestRateLimiterInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RequestRateLimiterInterceptor(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final int MAX_REQUESTS = 5;  // 每4秒最大请求次数
    private static final long TIME_WINDOW = 4L;  // 时间窗口（秒）
    private static final long BLOCK_TIME = 10L;  // 阻塞时间（秒）

    private static final String LOGIN_REQUIRED_PATH = "/user/";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = getUserIdFromRequest(request);
        String requestPath = request.getRequestURI();

        // 1. 检查用户是否登录
        if (isLoginRequired(requestPath) && (userId == null || !isUserLoggedIn(userId))) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("User not logged in");
            return false; // 拦截请求
        }

        // 2. 请求频率限制
        String rateLimiterKey = "rate_limit_" + userId;
        Long currentTime = Instant.now().getEpochSecond();

        // 获取请求时间戳列表
        List<String> requestTimestamps = redisTemplate.opsForList().range(rateLimiterKey, 0, -1);

        // 清除过期时间戳
        requestTimestamps.removeIf(timestamp -> currentTime - Long.parseLong(timestamp) > TIME_WINDOW);

        // 如果请求次数大于最大限制，进行拦截
        if (requestTimestamps.size() >= MAX_REQUESTS) {
            // 判断是否需要阻塞请求
            Long blockTime = Long.parseLong(Objects.requireNonNull(redisTemplate.opsForValue().get(rateLimiterKey + "_block_time")));
            if (currentTime < blockTime) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests, please wait 10 seconds");
                return false; // 拦截请求
            } else {
                // 重置请求次数，并清除阻塞标记
                redisTemplate.delete(rateLimiterKey + "_block_time");
            }
        }

        // 存储请求时间戳
        redisTemplate.opsForList().leftPush(rateLimiterKey, String.valueOf(currentTime));

        // 设置请求时间戳的过期时间
        redisTemplate.expire(rateLimiterKey, TIME_WINDOW, java.util.concurrent.TimeUnit.SECONDS);

        // 如果请求次数超出限制，设置阻塞时间
        if (requestTimestamps.size() >= MAX_REQUESTS) {
            redisTemplate.opsForValue().set(rateLimiterKey + "_block_time", String.valueOf(currentTime + BLOCK_TIME));
        }

        return true; // 继续处理请求
    }

    private boolean isLoginRequired(String requestPath) {
        return requestPath.startsWith(LOGIN_REQUIRED_PATH);
    }

    private boolean isUserLoggedIn(String userId) {
        // 根据实际情况判断用户是否登录
        return Boolean.TRUE.equals(redisTemplate.hasKey("user_logged_in_" + userId));
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        // 根据实际情况从请求中提取用户ID，通常是通过Session或JWT等方法获取
        return request.getHeader("user-id"); // 假设使用请求头传递用户ID
    }
}

