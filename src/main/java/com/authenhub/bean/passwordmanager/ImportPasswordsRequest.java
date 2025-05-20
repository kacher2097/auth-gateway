package com.authenhub.bean.passwordmanager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu nhập mật khẩu từ dữ liệu CSV hoặc JSON")
public class ImportPasswordsRequest {
    @Schema(description = "Định dạng của dữ liệu nhập (CSV hoặc JSON)", example = "CSV", allowableValues = {"CSV", "JSON"})
    private String format;

    @Schema(description = "Dữ liệu nhập dạng chuỗi (CSV hoặc JSON)", example = "id,site_url,username,password,icon_url\n1,https://example.com,user@example.com,password123,https://example.com/favicon.ico")
    private String importData;

    @Schema(description = "Danh sách các mật khẩu cần nhập (thay thế cho importData)")
    private List<PasswordImportItem> passwords;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Thông tin mật khẩu cần nhập")
    public static class PasswordImportItem {
        @Schema(description = "URL của trang web", example = "https://example.com", required = true)
        private String siteUrl;

        @Schema(description = "Tên đăng nhập hoặc email", example = "user@example.com", required = true)
        private String username;

        @Schema(description = "Mật khẩu cần lưu trữ (sẽ được mã hóa trước khi lưu trữ)", example = "StrongP@ssw0rd", required = true)
        private String password;

        @Schema(description = "URL của biểu tượng trang web", example = "https://example.com/favicon.ico")
        private String iconUrl;
    }
}
