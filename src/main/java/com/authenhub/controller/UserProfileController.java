package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.mongo.User;
import com.authenhub.service.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user profile operations
 */
@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserContext userContext;

    /**
     * Get the current user's profile
     *
     * @return the current user's profile
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCurrentUserProfile() {
        User currentUser = userContext.getCurrentUser();
        
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("User not authenticated")
                            .build());
        }
        
        // You can create a DTO to hide sensitive information if needed
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User profile retrieved successfully")
                .data(currentUser)
                .build());
    }
    
    /**
     * Check if the current user is an admin
     *
     * @return true if admin, false otherwise
     */
    @GetMapping("/is-admin")
    public ResponseEntity<ApiResponse> isAdmin() {
        if (!userContext.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("User not authenticated")
                            .build());
        }
        
        boolean isAdmin = userContext.isAdmin();
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message(isAdmin ? "User is an admin" : "User is not an admin")
                .data(isAdmin)
                .build());
    }
    
    /**
     * Get the current user's permissions
     *
     * @return the current user's permissions
     */
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse> getUserPermissions() {
        User currentUser = userContext.getCurrentUser();
        
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("User not authenticated")
                            .build());
        }
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User permissions retrieved successfully")
                .data(currentUser.getAuthorities())
                .build());
    }
}
