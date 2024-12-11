package org.yuxun.x.nexusx.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("users")
public class User {
    @TableId(value = "userId", type = IdType.AUTO)
    private Long userId;
    @TableField("username")
    private String username;
    @TableField("userAvatar")
    private String userAvatar;
    @TableField("password")
    private String password;
    @TableField("email")
    private String email;
    @TableField("phone")
    private String phone;
    @TableField("status")
    private Byte status;
    @TableField("lastLoginTime")
    private LocalDateTime lastLoginTime;
    @TableField("lastLoginIp")
    private String lastLoginIp;
    @TableField("createdAt")
    private LocalDateTime createdAt;
    @TableField("updateAt")
    private LocalDateTime updatedAt;
}
