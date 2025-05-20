package com.authenhub.bean.passwordmanager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu cập nhật mật khẩu")
public class PasswordUpdateRequest {
    @Schema(description = "URL mới của trang web", example = "https://example.com")
    private String siteUrl;

    @Schema(description = "Tên đăng nhập mới", example = "user@example.com")
    private String username;

    @Schema(description = "Mật khẩu mới (sẽ được mã hóa trước khi lưu trữ)", example = "NewStrongP@ssw0rd")
    private String password;

    @Schema(description = "URL mới của biểu tượng trang web", example = "https://example.com/favicon.ico")
    private String iconUrl;
}
