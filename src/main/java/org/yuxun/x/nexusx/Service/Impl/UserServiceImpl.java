package org.yuxun.x.nexusx.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.yuxun.x.nexusx.DTO.UserLoginDTO;
import org.yuxun.x.nexusx.DTO.UserRegistrationAndUpdateDTO;
import org.yuxun.x.nexusx.Entity.Devices;
import org.yuxun.x.nexusx.Entity.User;
import org.yuxun.x.nexusx.Entity.UserDeviceBinding;
import org.yuxun.x.nexusx.Mapper.DevicesMapper;
import org.yuxun.x.nexusx.Mapper.UserDeviceBindingMapper;
import org.yuxun.x.nexusx.Mapper.UserMapper;
import org.yuxun.x.nexusx.Response.UserLoginResponse;
import org.yuxun.x.nexusx.Service.UserService;
import org.yuxun.x.nexusx.Utils.AESUtil;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final DevicesMapper devicesMapper;
    private final UserDeviceBindingMapper userDeviceBindingMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public UserServiceImpl(UserMapper userMapper,DevicesMapper devicesMapper,UserDeviceBindingMapper userDeviceBindingMapper, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.devicesMapper = devicesMapper;
        this.userDeviceBindingMapper = userDeviceBindingMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 注册用户信息
     */
    @Override
    public void registerUser(UserRegistrationAndUpdateDTO registerDTO) {
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", registerDTO.getUsername())) != null) {
            throw new IllegalArgumentException("用户已存在");
        }
        if (userMapper.selectOne(new QueryWrapper<User>().eq("email", AESUtil.encrypt(registerDTO.getEmail()))) != null) {
            throw new IllegalArgumentException("该邮箱已注册");
        }
        if (userMapper.selectOne(new QueryWrapper<User>().eq("phone", AESUtil.encrypt(registerDTO.getPhone()))) != null) {
            throw new IllegalArgumentException("该手机已绑定");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User();
        String encryptedPhone = AESUtil.encrypt(registerDTO.getPhone());
        String encryptedEmail = AESUtil.encrypt(registerDTO.getEmail());
        String encryptedPassword = encoder.encode(registerDTO.getPassword());
        user.setUser_name(registerDTO.getUsername());
        user.setPassword(encryptedPassword);
        user.setEmail(encryptedEmail);
        user.setPhone(encryptedPhone);
        user.setUser_status((byte) 1);
        user.setCreated_at(String.valueOf(LocalDateTime.now()));
        user.setUpdated_at(String.valueOf(LocalDateTime.now()));

        int insert = userMapper.insert(user);
        if (insert <= 0 ){
            throw new RuntimeException("用户注册失败");
        }

    }

    /**
     * 修改用户信息
     */
    @Override
    public void updateUserInfo(UserRegistrationAndUpdateDTO registrationAndUpdateDTO) {
        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("user_id", registrationAndUpdateDTO.getUserId()));
        if (existingUser == null) {
            throw new IllegalArgumentException("当前用户状态出现异常！");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPhone = AESUtil.encrypt(registrationAndUpdateDTO.getPhone());
        String encryptedEmail = AESUtil.encrypt(registrationAndUpdateDTO.getEmail());
        String encryptedPassword = encoder.encode(registrationAndUpdateDTO.getPassword());

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", registrationAndUpdateDTO.getUserId()) // 更新条件
                .set("user_name", registrationAndUpdateDTO.getUsername())
                .set("password", encryptedPassword)
                .set("email", encryptedEmail)
                .set("phone", encryptedPhone)
                .set("user_status", (byte) 1)
                .set("updated_at", LocalDateTime.now());


        int update = userMapper.update(null, updateWrapper); // 不需要创建新的 User 对象
        if (update <= 0) {
            throw new RuntimeException("用户信息更新失败");
        }

    }

    /**
     * 用户登录
     */
    public UserLoginResponse loginUser(UserLoginDTO loginDTO) {
        // 双条件查询，优先使用邮箱登录，其次是手机号
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (loginDTO.getEmail() != null && !loginDTO.getEmail().isEmpty()) {
            queryWrapper.eq("email", AESUtil.encrypt(loginDTO.getEmail()));
        } else if (loginDTO.getPhone() != null && !loginDTO.getPhone().isEmpty()) {
            queryWrapper.eq("phone", AESUtil.encrypt(loginDTO.getPhone()));
        } else {
            throw new IllegalArgumentException("邮箱或手机号必须填写！");
        }

        // 查询用户信息
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在或信息错误！");
        }

        // 校验密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("密码错误！");
        }

        // 校验用户状态
        if (user.getUser_status() != 1) {
            throw new IllegalArgumentException("用户状态异常，无法登录！");
        }

        // 登录成功，生成会话ID并存入 Redis
        String sessionId = UUID.randomUUID().toString(); // 生成会话ID
        String sessionKey = "user:session:" + sessionId; // 存储在Redis中的key
        redisTemplate.opsForValue().set(sessionKey, user.getUser_id(), 30, TimeUnit.MINUTES); // 设置会话过期时间

        // 返回用户信息与会话ID
        UserLoginResponse response = new UserLoginResponse();
        response.setUserId(user.getUser_id());
        response.setUsername(user.getUser_name());
        response.setToken(sessionId); // 返回会话ID给前端
        return response;
    }

    /**
     * 用户登出
     */
    @Override
    public void logoutUser(String tokenId) {
        String sessionKey = "user:session:" + tokenId; // 获取存储会话的Redis键
        redisTemplate.delete(sessionKey); // 删除Redis中的会话信息
        System.out.println("User logged out successfully.");
    }

    /**
     * 根据邮箱或手机获取用户信息
     */
    @Override
    public UserLoginResponse getUserByPhoneOrEmail(UserLoginDTO userLoginDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userLoginDTO.getEmail() != null && !userLoginDTO.getEmail().isEmpty()) {
            queryWrapper.eq("email", AESUtil.encrypt(userLoginDTO.getEmail()));
        } else if (userLoginDTO.getPhone() != null && !userLoginDTO.getPhone().isEmpty()) {
            queryWrapper.eq("phone", AESUtil.encrypt(userLoginDTO.getPhone()));
        } else {
            throw new IllegalArgumentException("邮箱或手机号必须填写！");
        }
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new IllegalArgumentException("用户不存在或信息错误！");
        }

        // 转换 User 对象为 UserLoginResponse
        UserLoginResponse response = new UserLoginResponse();
        response.setUserId(user.getUser_id());
        response.setUsername(user.getUser_name());
        response.setEmail(user.getEmail()); // 如果需要
        response.setPhone(user.getPhone()); // 如果需要
        return response;
    }


    @Override
    public void userConnectedToDevice(Long userId, String deviceId) {
        // 检查设备是否已经绑定
        QueryWrapper<Devices> deviceQueryWrapper = new QueryWrapper<>();
        deviceQueryWrapper.eq("device_id", deviceId);
        Devices device = devicesMapper.selectOne(deviceQueryWrapper);

        if (device == null) {
            // 如果设备不存在，抛出异常
            throw new IllegalArgumentException("设备不存在！");
        }

        // 检查用户是否已经绑定设备
        QueryWrapper<UserDeviceBinding> bindingQueryWrapper = new QueryWrapper<>();
        bindingQueryWrapper.eq("user_id", userId).eq("device_id", AESUtil.encrypt(deviceId));
        UserDeviceBinding existingBinding = userDeviceBindingMapper.selectOne(bindingQueryWrapper);

        if (existingBinding != null) {
            // 如果设备已经绑定给当前用户，跳过或更新绑定信息
            existingBinding.setBindTime(LocalDateTime.now());
            userDeviceBindingMapper.updateById(existingBinding);
        } else {
            // 如果设备未绑定，插入新的绑定记录
            UserDeviceBinding newBinding = new UserDeviceBinding();
            newBinding.setUserId(userId);
            newBinding.setDeviceId(Byte.parseByte(deviceId));
            newBinding.setBindTime(LocalDateTime.now());

            int result = userDeviceBindingMapper.insert(newBinding);
            if (result <= 0) {
                throw new RuntimeException("设备绑定失败");
            }
        }

        // 设备绑定成功后，更新设备状态（可选）
        device.setUserId(userId);  // 记录绑定的用户
        devicesMapper.updateById(device);

        // 返回或执行其他操作，如更新缓存等
    }

}
