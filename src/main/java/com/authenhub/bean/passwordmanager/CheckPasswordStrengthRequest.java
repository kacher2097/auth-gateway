package com.authenhub.bean.passwordmanager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu kiểm tra độ mạnh của mật khẩu")
public class CheckPasswordStrengthRequest {
    @NotBlank(message = "Password is required")
    @Schema(description = "Mật khẩu cần kiểm tra độ mạnh", example = "MyP@ssw0rd", required = true)
    private String password;
}
