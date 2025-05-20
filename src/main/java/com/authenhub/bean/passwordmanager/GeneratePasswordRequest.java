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
@Schema(description = "Yêu cầu tạo mật khẩu ngẫu nhiên")
public class GeneratePasswordRequest {
    @Builder.Default
    @Schema(description = "Độ dài của mật khẩu", example = "16", defaultValue = "16")
    private int length = 16;

    @Builder.Default
    @Schema(description = "Bao gồm chữ thường (a-z)", example = "true", defaultValue = "true")
    private boolean includeLowercase = true;

    @Builder.Default
    @Schema(description = "Bao gồm chữ hoa (A-Z)", example = "true", defaultValue = "true")
    private boolean includeUppercase = true;

    @Builder.Default
    @Schema(description = "Bao gồm số (0-9)", example = "true", defaultValue = "true")
    private boolean includeNumbers = true;

    @Builder.Default
    @Schema(description = "Bao gồm ký tự đặc biệt (!@#$%^&*()_-+=<>?/[]{}|)", example = "true", defaultValue = "true")
    private boolean includeSpecial = true;
}
