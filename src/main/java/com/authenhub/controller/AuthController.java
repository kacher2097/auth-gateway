package com.authenhub.controller;

import com.authenhub.bean.*;
import com.authenhub.bean.auth.AuthRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.config.filter.JwtService;
import com.authenhub.service.interfaces.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API để xác thực người dùng: đăng ký, đăng nhập, đổi mật khẩu, quên mật khẩu")
public class AuthController {

    private final IAuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới",
            description = "Tạo một tài khoản mới với thông tin cơ bản của người dùng.")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập",
            description = "Đăng nhập vào hệ thống với tên đăng nhập và mật khẩu. Trả về token JWT để sử dụng cho các yêu cầu tiếp theo.")
    public ApiResponse<?> login(@Valid @RequestBody AuthRequest request) throws InvalidCredentialsException {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/social-login")
    @Operation(summary = "Đăng nhập bằng mạng xã hội",
            description = "Đăng nhập vào hệ thống bằng tài khoản mạng xã hội như Google, Facebook, v.v.")
    public ApiResponse<?> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
//        if (!rateLimiter.tryAcquire()) {
//            return ApiResponse.error("429", "Too many requests");
//        }
        return ApiResponse.success(authService.socialLogin(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Làm mới token",
            description = "Làm mới token JWT đã hết hạn để tiếp tục sử dụng mà không cần đăng nhập lại.")
    public ApiResponse<?> refreshToken(@RequestHeader("Authorization") String token) {
        return ApiResponse.success(authService.refreshToken(token));
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin người dùng hiện tại",
            description = "Lấy thông tin của người dùng đã đăng nhập dựa trên token JWT.")
    public ApiResponse<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ApiResponse.success(authService.getCurrentUser(token));
    }

    @PostMapping("/oauth2/callback")
    @Operation(summary = "Xử lý callback OAuth2",
            description = "Xử lý callback từ các dịch vụ OAuth2 như Google, Facebook, v.v.")
    public ApiResponse<?> oauthCallback(@Valid @RequestBody OAuth2CallbackRequest request) {
        return ApiResponse.success(authService.handleOAuth2Callback(request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu",
            description = "Đổi mật khẩu của người dùng đã đăng nhập. Yêu cầu cung cấp mật khẩu cũ và mật khẩu mới.")
    public ApiResponse<?> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu",
            description = "Gửi email đặt lại mật khẩu cho người dùng đã quên mật khẩu.")
    public ApiResponse<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu",
            description = "Đặt lại mật khẩu của người dùng sau khi đã nhận được email đặt lại mật khẩu.")
    public ApiResponse<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(null);
    }
}