package com.authenhub.controller;

import com.authenhub.bean.ChangePasswordRequest;
import com.authenhub.bean.ForgotPasswordRequest;
import com.authenhub.bean.OAuth2CallbackRequest;
import com.authenhub.bean.RegisterRequest;
import com.authenhub.bean.ResetPasswordRequest;
import com.authenhub.bean.SocialLoginRequest;
import com.authenhub.dto.*;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/social-login")
    public ResponseEntity<AuthResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
//        if (!rateLimiter.tryAcquire()) {
//            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
//        }
        return ResponseEntity.ok(authService.socialLogin(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("User not authenticated")
                    .build());
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Invalid authentication principal")
                    .build());
        }

        User user = (User) authentication.getPrincipal();

        // Tạo token mới
        String newToken = jwtService.generateToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", newToken);
        data.put("username", user.getUsername());

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Token refreshed successfully")
                .data(data)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.getCurrentUser(token));
    }

    @PostMapping("/oauth2/callback")
    public ResponseEntity<AuthResponse> oauthCallback(@Valid @RequestBody OAuth2CallbackRequest request) {
        return ResponseEntity.ok(authService.handleOAuth2Callback(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest request) {
        // Extract username from token
        String jwt = token.substring(7); // Remove "Bearer " prefix
        String username = jwtService.extractUsername(jwt);

        authService.changePassword(username, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-role")
    public ResponseEntity<ApiResponse> checkRole(@RequestHeader("Authorization") String token) {
        // Extract token from Authorization header
        String jwt = token.substring(7); // Remove "Bearer " prefix

        // Get user role from token
        String role = jwtService.extractRole(jwt);
        boolean isAdmin = role != null && role.equals("ROLE_ADMIN");

        Map<String, Object> data = new HashMap<>();
        data.put("role", role);
        data.put("isAdmin", isAdmin);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Role check successful")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}