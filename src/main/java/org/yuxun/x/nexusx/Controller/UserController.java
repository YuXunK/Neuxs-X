package org.yuxun.x.nexusx.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yuxun.x.nexusx.DTO.UserLoginDTO;
import org.yuxun.x.nexusx.DTO.UserRegistrationAndUpdateDTO;
import org.yuxun.x.nexusx.Response.UserLoginResponse;
import org.yuxun.x.nexusx.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationAndUpdateDTO registerDTO) {
        try {
            userService.registerUser(registerDTO);
            return ResponseEntity.ok("注册成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("用户注册失败");
        }
    }

    // 用户更新信息
    @PutMapping("/update")
    public ResponseEntity<String> updateUserInfo(@RequestBody UserRegistrationAndUpdateDTO registrationAndUpdateDTO) {
        try {
            userService.updateUserInfo(registrationAndUpdateDTO);
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
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // 用户登出
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestParam String tokenId) {
        try {
            userService.logoutUser(tokenId);
            return ResponseEntity.ok("用户登出成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("用户登出失败");
        }
    }

    // 根据手机号或邮箱获取用户信息
    @GetMapping("/getUserByPhoneOrEmail")
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

}
