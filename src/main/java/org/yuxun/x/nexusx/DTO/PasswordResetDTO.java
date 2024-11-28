package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "密码重置请求对象")
public class PasswordResetDTO {

    @Schema(description = "用户邮箱或联系电话", example = "john.doe@example.com", required = true)
    private String EmailOrPhone;

    @Schema(description = "新密码", example = "newpassword123", required = true)
    private String newPassword;
}

