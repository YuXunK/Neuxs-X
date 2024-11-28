package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "用户登录请求数据")
public class UserLoginDTO {

    @Schema(description = "用户邮箱")
    private String email;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "用户密码")
    private String password;

    // Getters and Setters
}
