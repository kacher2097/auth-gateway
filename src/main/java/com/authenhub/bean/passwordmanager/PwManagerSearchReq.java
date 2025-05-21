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
@Schema(description = "Yêu cầu tìm kiếm mật khẩu với phân trang")
public class PwManagerSearchReq {
    @Schema(description = "Từ khóa tìm kiếm (tìm trong URL và tên người dùng)", example = "gmail")
    private String keyword;

    @Schema(description = "Nhà cung cấp dịch vụ", example = "google")
    private String provider;

    @Schema(description = "Tên người dùng", example = "user@example.com")
    private String username;

    @Schema(description = "Email", example = "user@example.com")
    private String email;

    @Builder.Default
    @Schema(description = "Số trang (bắt đầu từ 0)", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Builder.Default
    @Schema(description = "Kích thước trang", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Builder.Default
    @Schema(description = "Sắp xếp theo trường", example = "createdAt", defaultValue = "createdAt",
            allowableValues = {"id", "siteUrl", "username", "createdAt", "updatedAt"})
    private String sortBy = "createdAt";

    @Builder.Default
    @Schema(description = "Hướng sắp xếp", example = "DESC", defaultValue = "DESC",
            allowableValues = {"ASC", "DESC"})
    private String sortDirection = "DESC";
}
