package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("users")
public class User {
    private Long user_id;
    private String user_name;
    private String password;
    private String email;
    private String phone;
    private Byte user_status;
    private LocalDateTime last_login_time;
    private String last_login_ip;
    private String created_at;
    private String updated_at;
}
