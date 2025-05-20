package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.passwordmanager.*;
import com.authenhub.service.PasswordManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
@Tag(name = "Password Manager", description = "API để quản lý mật khẩu: tạo, đọc, cập nhật, xóa, tạo mật khẩu ngẫu nhiên, kiểm tra độ mạnh, nhập/xuất mật khẩu")
@SecurityRequirement(name = "bearerAuth")
public class PasswordManagerController {

    private final PasswordManagerService passwordManagerService;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('password:read')")
    @Operation(summary = "Lấy danh sách tất cả mật khẩu",
            description = "Trả về danh sách tất cả các mật khẩu đã lưu trữ. Mật khẩu sẽ không được giải mã trong kết quả trả về.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content)
    })
    public ApiResponse<List<PasswordResponse>> getAllPasswords() {
        log.info("Getting all passwords");
        return passwordManagerService.getAllPasswords();
    }

    @PostMapping("/get")
    @PreAuthorize("hasAuthority('password:read')")
    @Operation(summary = "Lấy mật khẩu theo ID",
            description = "Trả về thông tin mật khẩu theo ID. Có thể chọn bao gồm hoặc không bao gồm mật khẩu đã giải mã trong kết quả trả về.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy mật khẩu",
                    content = @Content)
    })
    public ApiResponse<PasswordResponse> getPasswordById(
            @Parameter(description = "ID của mật khẩu cần lấy", required = true)
            @RequestParam Long id,
            @Parameter(description = "Có bao gồm mật khẩu đã giải mã trong kết quả hay không", required = false)
            @RequestParam(defaultValue = "false") boolean includePassword) {
        log.info("Getting password by ID: {}, includePassword: {}", id, includePassword);
        return passwordManagerService.getPasswordById(id, includePassword);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('password:read')")
    @Operation(summary = "Tìm kiếm mật khẩu",
            description = "Tìm kiếm mật khẩu theo các tiêu chí như từ khóa, tên người dùng, v.v. Mật khẩu sẽ không được giải mã trong kết quả trả về.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content)
    })
    public ApiResponse<?> searchPasswords(
            @Parameter(description = "Yêu cầu tìm kiếm với các tiêu chí", required = true)
            @RequestBody PwManagerSearchReq request) {
        log.info("Searching passwords with request: {}", request);
        return passwordManagerService.searchPassword(request);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('password:create')")
    @Operation(summary = "Tạo mật khẩu mới",
            description = "Tạo một mật khẩu mới với thông tin trang web, tên người dùng và mật khẩu. Mật khẩu sẽ được mã hóa trước khi lưu trữ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Mật khẩu đã tồn tại",
                    content = @Content)
    })
    public ApiResponse<PasswordResponse> createPassword(
            @Parameter(description = "Thông tin mật khẩu cần tạo", required = true)
            @Valid @RequestBody PasswordCreateRequest request) {
        log.info("Creating password for site: {}, username: {}", request.getSiteUrl(), request.getUsername());
        return passwordManagerService.createPassword(request);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('password:update')")
    @Operation(summary = "Cập nhật mật khẩu",
            description = "Cập nhật thông tin mật khẩu hiện có. Có thể cập nhật trang web, tên người dùng, mật khẩu hoặc biểu tượng. Mật khẩu mới sẽ được mã hóa trước khi lưu trữ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy mật khẩu",
                    content = @Content)
    })
    public ApiResponse<PasswordResponse> updatePassword(
            @Parameter(description = "ID của mật khẩu cần cập nhật", required = true)
            @RequestParam Long id,
            @Parameter(description = "Thông tin mật khẩu cần cập nhật", required = true)
            @RequestBody PasswordUpdateRequest request) {
        log.info("Updating password with ID: {}", id);
        return passwordManagerService.updatePassword(id, request);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('password:delete')")
    @Operation(summary = "Xóa mật khẩu",
            description = "Xóa mật khẩu theo ID. Hành động này không thể hoàn tác.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy mật khẩu",
                    content = @Content)
    })
    public ApiResponse<?> deletePassword(
            @Parameter(description = "ID của mật khẩu cần xóa", required = true)
            @RequestParam Long id) {
        log.info("Deleting password with ID: {}", id);
        return passwordManagerService.deletePassword(id);
    }

    @PostMapping("/generate")
    @Operation(summary = "Tạo mật khẩu ngẫu nhiên",
            description = "Tạo mật khẩu ngẫu nhiên với các tùy chọn về độ dài và loại ký tự. Mật khẩu được tạo sẽ đảm bảo độ mạnh và tính ngẫu nhiên.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ",
                    content = @Content)
    })
    public ApiResponse<GeneratePasswordResponse> generatePassword(
            @Parameter(description = "Các tùy chọn để tạo mật khẩu ngẫu nhiên", required = true)
            @RequestBody GeneratePasswordRequest request) {
        log.info("Generating random password with length: {}", request.getLength());
        return passwordManagerService.generatePassword(request);
    }

    @PostMapping("/check-strength")
    @Operation(summary = "Kiểm tra độ mạnh của mật khẩu",
            description = "Kiểm tra độ mạnh của mật khẩu và cung cấp điểm số, mức độ mạnh và các gợi ý cải thiện.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ",
                    content = @Content)
    })
    public ApiResponse<CheckPasswordStrengthResponse> checkPasswordStrength(
            @Parameter(description = "Mật khẩu cần kiểm tra độ mạnh", required = true)
            @Valid @RequestBody CheckPasswordStrengthRequest request) {
        log.info("Checking password strength");
        return passwordManagerService.checkPasswordStrength(request);
    }

    @PostMapping("/export")
    @PreAuthorize("hasAuthority('password:export')")
    @Operation(summary = "Xuất mật khẩu",
            description = "Xuất danh sách mật khẩu theo định dạng CSV hoặc JSON. Có thể chọn bao gồm hoặc không bao gồm mật khẩu đã giải mã trong kết quả xuất.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content)
    })
    public ApiResponse<ExportPasswordsResponse> exportPasswords(
            @Parameter(description = "Định dạng xuất (CSV hoặc JSON)", required = false)
            @RequestParam(defaultValue = "JSON") String format,
            @Parameter(description = "Có bao gồm mật khẩu đã giải mã trong kết quả xuất hay không", required = false)
            @RequestParam(defaultValue = "false") boolean includePasswords) {
        log.info("Exporting passwords in format: {}, includePasswords: {}", format, includePasswords);
        return passwordManagerService.exportPasswords(format, includePasswords);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('password:import')")
    @Operation(summary = "Nhập mật khẩu",
            description = "Nhập danh sách mật khẩu từ định dạng CSV hoặc JSON. Mật khẩu sẽ được mã hóa trước khi lưu trữ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.authenhub.bean.common.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content)
    })
    public ApiResponse<ImportPasswordsResponse> importPasswords(
            @Parameter(description = "Yêu cầu nhập mật khẩu với dữ liệu và định dạng", required = true)
            @RequestBody ImportPasswordsRequest request) {
        log.info("Importing passwords from format: {}", request.getFormat());
        return passwordManagerService.importPasswords(request);
    }
}
