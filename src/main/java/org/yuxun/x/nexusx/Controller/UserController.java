package org.yuxun.x.nexusx.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yuxun.x.nexusx.DTO.UserLoginDTO;
import org.yuxun.x.nexusx.DTO.UserNormalDTO;
import org.yuxun.x.nexusx.DTO.UserRegistrationDTO;
import org.yuxun.x.nexusx.Entity.User;
import org.yuxun.x.nexusx.Response.UserLoginResponse;
import org.yuxun.x.nexusx.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO userDto) {
        String result = userService.registerUser(userDto);
        if ("注册成功".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 用户更新信息
    @PutMapping("/update")
    public ResponseEntity<String> updateUserInfo(@RequestBody UserNormalDTO userNormalDTO) {
        try {
            userService.updateUserInfo(userNormalDTO);
            return ResponseEntity.ok("用户信息更新成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("用户信息更新失败");
        }
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@RequestBody UserLoginDTO loginDTO) {
        try {
            UserLoginResponse loginResponse = userService.loginUser(loginDTO);

            // 检查是否有错误信息
            if (loginResponse.getMisTakeMessage() != null) {
                // 登录失败，返回 400 错误及错误信息
                return ResponseEntity.badRequest().body(loginResponse);
            }

            // 登录成功，返回 200 和登录响应数据
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            // 捕获非法参数异常，返回 400 错误
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // 捕获其他异常，返回 500 错误
            return ResponseEntity.status(500).body(null);
        }
    }

    // 用户登出
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestParam String userId) {
        try {
            userService.logoutUser(userId);
            return ResponseEntity.ok("用户登出成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("用户登出失败");
        }
    }

    // 根据手机号或邮箱获取用户信息
    @PostMapping("/getUserByPhoneOrEmail")
    public ResponseEntity<UserLoginResponse> getUserByPhoneOrEmail(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            UserLoginResponse userResponse = userService.getUserByPhoneOrEmail(userLoginDTO);
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 用户绑定设备
    @PostMapping("/bindDevice")
    public ResponseEntity<String> userConnectedToDevice(@RequestParam Long userId, @RequestParam String deviceId) {
        try {
            userService.userConnectedToDevice(userId, deviceId);
            return ResponseEntity.ok("设备绑定成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("设备绑定失败");
        }
    }

    /**
     * 在localstorage存活期间，可以通过storage中内容从Redis中获取用户信息
     */
    @PostMapping("/getUserInfoByRedis")
    public ResponseEntity<User> getUserInfoByRedis(@RequestParam String sessionKey) {
        try {
            User user = userService.getUserInfoByRedis(sessionKey);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
