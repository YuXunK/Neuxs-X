package org.yuxun.x.nexusx.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yuxun.x.nexusx.DTO.UserLoginDTO;
import org.yuxun.x.nexusx.DTO.UserNormalDTO;
import org.yuxun.x.nexusx.DTO.UserRegistrationDTO;
import org.yuxun.x.nexusx.Entity.Devices;
import org.yuxun.x.nexusx.Entity.User;
import org.yuxun.x.nexusx.Entity.UserDeviceBinding;
import org.yuxun.x.nexusx.Mapper.DevicesMapper;
import org.yuxun.x.nexusx.Mapper.UserDeviceBindingMapper;
import org.yuxun.x.nexusx.Mapper.UserMapper;
import org.yuxun.x.nexusx.Response.UserLoginResponse;
import org.yuxun.x.nexusx.Service.UserService;
import org.yuxun.x.nexusx.Utils.AESUtil;
import org.yuxun.x.nexusx.Utils.RedisSessionHandler;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final DevicesMapper devicesMapper;
    private final UserDeviceBindingMapper userDeviceBindingMapper;
    public static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final RedisSessionHandler redisSessionHandler;
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    public UserServiceImpl(UserMapper userMapper, DevicesMapper devicesMapper, UserDeviceBindingMapper userDeviceBindingMapper, RedisSessionHandler redisSessionHandler,RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.devicesMapper = devicesMapper;
        this.userDeviceBindingMapper = userDeviceBindingMapper;
        this.redisSessionHandler = redisSessionHandler;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 注册用户信息
     * @author yuxun
     */
    @Override
    public String registerUser(UserRegistrationDTO userDto) {
        AESUtil encoder = new AESUtil();
        // 检查用户名是否已存在（通过加密后的用户名查重）
        if (userMapper.existsByUsername(userDto.getUsername())) {
            return "用户名已存在";
        }

        // 检查手机号是否已存在（通过加密后手机号查重）
        if (userMapper.existsByPhone(AESUtil.encrypt(userDto.getPhone()))) {
            return "手机号码已存在";
        }

        // 检查邮箱是否已存在（通过加密后邮箱查重）
        if (userMapper.existsByEmail(AESUtil.encrypt(userDto.getEmail()))) {
            return "邮箱已存在";
        }

        // 如果通过所有查重逻辑，则插入用户信息
        User user = new User();
        user.setUsername(userDto.getUsername()); // 加密用户名
        user.setPhone(encoder.encode(userDto.getPhone()));       // 加密手机号
        user.setEmail(encoder.encode(userDto.getEmail()));       // 加密邮箱
        user.setPassword(encoder.encode(userDto.getPassword()));   // 加密密码
        user.setStatus((byte) 1);                                // 设置用户默认状态为启用
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 插入用户到数据库
        userMapper.insertUser(user);

        return "注册成功";
    }

    /**
     * 修改用户信息 ---初版待修改
     * @author yuxun
     */
    @Override
    public void updateUserInfo(UserNormalDTO registrationAndUpdateDTO) {
        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("user_id", registrationAndUpdateDTO.getUserId()));
        if (existingUser == null) {
            throw new IllegalArgumentException("当前用户状态出现异常！");
        }

        String encryptedPhone = AESUtil.encrypt(registrationAndUpdateDTO.getPhone());
        String encryptedEmail = AESUtil.encrypt(registrationAndUpdateDTO.getEmail());
        String encryptedPassword = AESUtil.encrypt(registrationAndUpdateDTO.getPassword());

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
     * @author yuxun
     */
    public UserLoginResponse loginUser(UserLoginDTO loginDTO) {
        AESUtil encoder = new AESUtil();
        System.out.println("调用方法findByPhoneOrEmail,查询对象为："+loginDTO);
        User user = userMapper.findByPhoneOrEmail(encoder.encode(loginDTO.getPhoneOrEmail()));
        UserLoginResponse response = new UserLoginResponse();
        if (user == null || user.getUserId() == null) {
            response.setMisTakeMessage("对象用户不存在");
            return response;
        }

        if (!encoder.matches(loginDTO.getPassword(), user.getPassword())) {
            response.setMisTakeMessage("密码验证错误!");
            return response;
        }

        // 校验用户状态
        if (user.getStatus() != 1) {
            response.setMisTakeMessage("对象用户已被封号，如有问题请联系客服");
            return response;
        }

        // 登录成功，生成会话ID（Redis 操作不在事务内）
        String sessionId = UUID.randomUUID().toString(); // 生成会话ID

        // 返回用户信息与会话ID
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setToken(sessionId); // 返回会话ID给前端
        // 异步或单独处理 Redis 操作
        redisSessionHandler.handleSessionInRedisAsync(user.getUserId(), user);

        return response;
    }

    /**
     * 用户登出
     * @author yuxun
     */
    @Override
    public void logoutUser(String userId) {
        String sessionKey = "user:session:" + userId; // 获取存储会话的Redis键
        if (Boolean.TRUE.equals(redisTemplate.delete(sessionKey))){
            // 删除Redis中的会话信息
            System.out.println("User logged out successfully.");
        }
    }

    /**
     * 根据邮箱或手机获取用户信息
     * @author yuxun
     */
    @Override
    public UserLoginResponse getUserByPhoneOrEmail(UserLoginDTO userLoginDTO) {
        AESUtil encoder = new AESUtil();
        User user = userMapper.findByPhoneOrEmail(encoder.encode(userLoginDTO.getPhoneOrEmail()));

        // 创建响应对象
        UserLoginResponse response = new UserLoginResponse();

        // 如果用户对象为 null，返回空响应
        if (user == null) {
            return response;  // 这里返回空的 UserLoginResponse 对象
        }

        // 如果找到用户，填充响应对象
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail()); // 如果需要
        response.setPhone(user.getPhone()); // 如果需要

        return response;
    }

    /**
     * @author yuxun
     * 通过userId这个key从存活期间的Redis中获取对应user信息
     * 由于对象存储结果序列化后，通过自定义逆序列化工具返回对应User
     */
    @Override
    public User getUserInfoByRedis(String userId) {
        User user = redisSessionHandler.getUserFromSession(userId);
        if (user != null) {
            System.out.println("UserName:" + user.getUsername());
            return user;
        } else {
            System.out.println("TargetSession is gone");
            throw new IllegalArgumentException("User Session is gone");
        }
    }

    /**
     * @author yuxun
     * @描述:  通过前端提交数据体交由后端处理，首先是将文件存储到后端资源目录下指定位置
     * 再将相对路径存储到数据库中保存
     */
    @Override
    public void userAddAvatar(UserNormalDTO normalDTO) {
        if (normalDTO == null || normalDTO.getUserId() == null || normalDTO.getUserAvatar() == null) {
            throw new IllegalArgumentException("Invalid input: User ID and avatar path are required.");
        }

        // 更新数据库中的头像路径
        User user = userMapper.selectById(normalDTO.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + normalDTO.getUserId());
        }

        user.setUserAvatar(normalDTO.getUserAvatar());
        user.setUpdatedAt(LocalDateTime.now()); // 更新修改时间

        // 保存到数据库
        int rows = userMapper.updateById(user);
        if (rows == 0) {
            throw new RuntimeException("Failed to update avatar for user ID: " + normalDTO.getUserId());
        }
    }

    /**
     * 用于完成用户与设备间的绑定，当前仅为初版后续还会进行调整
     * @author yuxun
     */
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
