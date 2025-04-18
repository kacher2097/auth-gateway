package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.User;
import com.authenhub.service.UserContext;
import com.authenhub.service.interfaces.IUserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user management operations
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final IUserManagementService userManagementService;
    private final UserContext userContext;

    /**
     * Get the current user's profile
     *
     * @return the current user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser() {
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
                .message("User profile retrieved successfully")
                .data(currentUser)
                .build());
    }
    
    /**
     * Update the current user's profile
     *
     * @param request update request
     * @return updated user
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse> updateCurrentUser(@RequestBody UserUpdateRequest request) {
        User currentUser = userContext.getCurrentUser();
        
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("User not authenticated")
                            .build());
        }
        
        try {
            User updatedUser = userManagementService.updateUser(currentUser.getId(), request);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User profile updated successfully")
                    .data(updatedUser)
                    .build());
        } catch (Exception e) {
            log.error("Error updating user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error updating user profile: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Get a user by ID
     *
     * @param userId user ID
     * @return user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String userId) {
        // Check if user is authenticated
        if (!userContext.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("User not authenticated")
                            .build());
        }
        
        // Only admins or the user themselves can view user details
        User currentUser = userContext.getCurrentUser();
        if (!userContext.isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("You don't have permission to view this user")
                            .build());
        }
        
        try {
            User user = userManagementService.getCurrentUser();
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User retrieved successfully")
                    .data(user)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error retrieving user: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Update a user by ID
     *
     * @param userId user ID
     * @param request update request
     * @return updated user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        try {
            User updatedUser = userManagementService.updateUser(userId, request);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User updated successfully")
                    .data(updatedUser)
                    .build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error updating user: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Set user active status
     *
     * @param userId user ID
     * @param active active status
     * @return updated user
     */
    @PutMapping("/{userId}/active")
    public ResponseEntity<ApiResponse> setUserActiveStatus(@PathVariable String userId, @RequestParam boolean active) {
        try {
            User updatedUser = userManagementService.setUserActiveStatus(userId, active);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User active status updated successfully")
                    .data(updatedUser)
                    .build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating user active status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error updating user active status: " + e.getMessage())
                            .build());
        }
    }
    
    /**
     * Set user role
     *
     * @param userId user ID
     * @param role role
     * @return updated user
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse> setUserRole(@PathVariable String userId, @RequestParam User.Role role) {
        try {
            User updatedUser = userManagementService.setUserRole(userId, role);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User role updated successfully")
                    .data(updatedUser)
                    .build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error updating user role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error updating user role: " + e.getMessage())
                            .build());
        }
    }
}
