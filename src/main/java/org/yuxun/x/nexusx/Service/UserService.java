package org.yuxun.x.nexusx.Service;

import org.yuxun.x.nexusx.DTO.UserLoginDTO;
import org.yuxun.x.nexusx.DTO.UserNormalDTO;
import org.yuxun.x.nexusx.DTO.UserRegistrationDTO;
import org.yuxun.x.nexusx.Entity.User;
import org.yuxun.x.nexusx.Response.UserLoginResponse;

public interface UserService {
    String registerUser(UserRegistrationDTO registerDTO);
    void updateUserInfo(UserNormalDTO user);
    UserLoginResponse loginUser(UserLoginDTO user);
    void logoutUser(String tokenId);
    UserLoginResponse getUserByPhoneOrEmail(UserLoginDTO userLoginDTO);
    void userConnectedToDevice(Long userId, String deviceId);
    void userAddAvatar(UserNormalDTO normalDTO);
    User getUserInfoByRedis(String userId);
}
