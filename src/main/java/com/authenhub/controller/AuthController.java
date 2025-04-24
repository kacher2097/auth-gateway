package com.authenhub.controller;

import com.authenhub.bean.*;
import com.authenhub.bean.auth.AuthRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.filter.JwtService;
import com.authenhub.service.interfaces.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/social-login")
    public ApiResponse<?> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
//        if (!rateLimiter.tryAcquire()) {
//            return ApiResponse.error("429", "Too many requests");
//        }
        return ApiResponse.success(authService.socialLogin(request));
    }

    @PostMapping("/refresh-token")
    public ApiResponse<?> refreshToken(@RequestHeader("Authorization") String token) {
        return ApiResponse.success(authService.refreshToken(token));
    }

    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ApiResponse.success(authService.getCurrentUser(token));
    }

    @PostMapping("/oauth2/callback")
    public ApiResponse<?> oauthCallback(@Valid @RequestBody OAuth2CallbackRequest request) {
        return ApiResponse.success(authService.handleOAuth2Callback(request));
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/check-role")
    public ApiResponse<?> checkRole(@RequestHeader("Authorization") String token) {
        // Extract token from Authorization header
        String jwt = token.substring(7); // Remove "Bearer " prefix

        // Get user role from token
        String role = jwtService.extractRole(jwt);
        boolean isAdmin = role != null && role.equals("ROLE_ADMIN");

        Map<String, Object> data = new HashMap<>();
        data.put("role", role);
        data.put("isAdmin", isAdmin);

        return ApiResponse.success(data);
    }
}