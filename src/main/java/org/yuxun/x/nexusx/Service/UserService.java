package org.yuxun.x.nexusx.Service;

import org.yuxun.x.nexusx.DTO.UserLoginDTO;
import org.yuxun.x.nexusx.DTO.UserRegistrationAndUpdateDTO;
import org.yuxun.x.nexusx.Entity.User;
import org.yuxun.x.nexusx.Response.UserLoginResponse;

public interface UserService {
    public void registerUser(UserRegistrationAndUpdateDTO registerDTO);
    public void updateUserInfo(UserRegistrationAndUpdateDTO user);
    public UserLoginResponse loginUser(UserLoginDTO user);
    public void logoutUser(String tokenId);
    public UserLoginResponse getUserByPhoneOrEmail(UserLoginDTO userLoginDTO);
    public void userConnectedToDevice(Long userId, String deviceId);

}
