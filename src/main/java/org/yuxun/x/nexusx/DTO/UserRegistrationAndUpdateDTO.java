package org.yuxun.x.nexusx.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "用户注册传输对象")
public class UserRegistrationAndUpdateDTO {

    @Schema(description = "用户ID", example = "123")
    @NotBlank
    private long userId;

    @Schema(description = "用户名",example = "NIK")
    @NonNull
    @NotBlank
    private String username;

    @Schema(description = "邮箱地址",example = "nik1226@example.com")
    @Email
    private String email;

    @Schema(description = "联系电话",example = "18528151667")
    @Size(min=11,max=11)
    private String phone;

    @Schema(description = "用户密码",example = "password123")
    @Size(min=8,max=24)
    private String password;


}
