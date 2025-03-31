package com.authenhub.controller;

import com.authenhub.dto.AuthRequest;
import com.authenhub.dto.AuthResponse;
import com.authenhub.dto.RegisterRequest;
import com.authenhub.dto.SocialLoginRequest;
import com.authenhub.entity.User;
import com.authenhub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

//    @GetMapping("/oauth2/google")
//    public ResponseEntity<AuthResponse> googleLogin(@AuthenticationPrincipal OAuth2User principal) {
//        return ResponseEntity.ok(authService.socialLogin(request));
//    }
//
//    @GetMapping("/oauth2/facebook")
//    public ResponseEntity<AuthResponse> facebookLogin(@AuthenticationPrincipal OAuth2User principal) {
//        return ResponseEntity.ok(authService.socialLogin(request));
//    }

    @PostMapping("/social-login")
    public ResponseEntity<AuthResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authService.socialLogin(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserInfo> getCurrentUser(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(AuthResponse.UserInfo.fromUser(user));
    }
} 