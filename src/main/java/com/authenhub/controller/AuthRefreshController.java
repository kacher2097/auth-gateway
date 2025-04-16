package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRefreshController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

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
}
