package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "用户基础操作请求数据")
public class UserNormalDTO {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像地址")
    private String userAvatar;

    @Schema(description = "用户密码")
    private String password;

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "请求执行情况")
    private int requestCode;

    @Schema(description = "信息更新时间")
    private LocalDateTime updatedAt;

    // Getters and Setters
}
