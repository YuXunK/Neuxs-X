package org.yuxun.x.nexusx.Model;

import lombok.Getter;
import lombok.Setter;
import org.yuxun.x.nexusx.DTO.UserNormalDTO;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserModel {
    private Long user_id;
    private String user_name;
    private String password;
    private String email;
    private String phone;
    private Byte status;
    private LocalDateTime last_login;
    private String last_login_ip;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public UserModel(Long user_id,String user_name, String phone,String email,String password,String last_login_ip,LocalDateTime last_login, LocalDateTime created_at, LocalDateTime updated_at, Byte status) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.last_login = last_login;
        this.last_login_ip = last_login_ip;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }

    /*邮箱格式验证*/
    public boolean validateEmail() {
        return email != null && !email.contains("@");
    }

    /*数据转换方法（DTO<-->Model）*/
    public UserNormalDTO toDTO() {
        UserNormalDTO userDTO = new UserNormalDTO();
        userDTO.setUsername(this.user_name);
        userDTO.setEmail(this.email);
        userDTO.setPassword(this.password);
        userDTO.setPhone(this.phone);
        return userDTO;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + user_id +
                ", username='" + user_name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
