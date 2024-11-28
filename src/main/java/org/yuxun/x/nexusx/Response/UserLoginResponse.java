package org.yuxun.x.nexusx.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "登录注册响应数据")
public class UserLoginResponse {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "邮箱", format = "email")
    private String email;

    @Schema(description = "电话", format = "phone")
    private String phone;

    @Schema(description = "会话令牌")
    private String token;
}
