package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.mongo.User;
import com.authenhub.filter.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/debug")
@RequiredArgsConstructor
public class AuthDebugController {

    private final JwtService jwtService;

    @GetMapping("/authorities")
    public ResponseEntity<ApiResponse> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> data = new HashMap<>();
        data.put("principal", authentication.getPrincipal().getClass().getName());
        data.put("authenticated", authentication.isAuthenticated());

        if (authentication.getPrincipal() instanceof User user) {
            data.put("username", user.getUsername());
            data.put("legacyRole", user.getRole());
            data.put("roleIds", user.getRoleId());
        }

        data.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Current user authorities retrieved successfully")
                .data(data)
                .build());
    }

    @GetMapping("/token")
    public ResponseEntity<ApiResponse> decodeToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Invalid Authorization header")
                    .build());
        }

        String token = authHeader.substring(7);
        Map<String, Object> data = new HashMap<>();

        try {
            data.put("username", jwtService.extractUsername(token));
            data.put("roles", jwtService.extractClaim(token, claims -> claims.get("roles")));
            data.put("permissions", jwtService.extractClaim(token, claims -> claims.get("permissions")));
            data.put("userId", jwtService.extractClaim(token, claims -> claims.get("userId")));
            data.put("email", jwtService.extractClaim(token, claims -> claims.get("email")));
            data.put("fullName", jwtService.extractClaim(token, claims -> claims.get("fullName")));
            data.put("expiration", jwtService.extractExpiration(token));
            data.put("isExpired", jwtService.extractClaim(token, claims ->
                    jwtService.extractExpiration(token).before(new java.util.Date())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Error decoding token: " + e.getMessage())
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Token decoded successfully")
                .data(data)
                .build());
    }
}
