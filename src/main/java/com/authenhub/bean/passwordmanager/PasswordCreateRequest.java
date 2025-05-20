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
@Schema(description = "Yêu cầu tạo mật khẩu mới")
public class PasswordCreateRequest {
    @NotBlank(message = "Site URL is required")
    @Schema(description = "URL của trang web", example = "https://example.com", required = true)
    private String siteUrl;

    @NotBlank(message = "Username is required")
    @Schema(description = "Tên đăng nhập hoặc email dùng để đăng nhập vào trang web", example = "user@example.com", required = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Mật khẩu cần lưu trữ (sẽ được mã hóa trước khi lưu trữ)", example = "StrongP@ssw0rd", required = true)
    private String password;

    @Schema(description = "URL của biểu tượng trang web", example = "https://example.com/favicon.ico")
    private String iconUrl;
}
