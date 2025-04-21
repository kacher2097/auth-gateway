package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.mongo.User;
import com.authenhub.service.interfaces.IUserManagementService;
import com.authenhub.service.interfaces.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user operations
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserManagementService userManagementService;
    private final IUserRoleService userRoleService;

    /**
     * Update user
     *
     * @param userId user id
     * @param request user update request
     * @return updated user
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateRequest request) {
        
        User updatedUser = userManagementService.updateUser(userId, request);
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build());
    }
    
    /**
     * Set user active status
     *
     * @param userId user id
     * @param active active status
     * @return updated user
     */
    @PutMapping("/{userId}/active")
    public ResponseEntity<ApiResponse> setUserActiveStatus(
            @PathVariable String userId,
            @RequestParam boolean active) {
        
        User updatedUser = userManagementService.setUserActiveStatus(userId, active);
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User active status updated successfully")
                .data(updatedUser)
                .build());
    }
    
    /**
     * Set user role
     *
     * @param userId user id
     * @param role user role
     * @return updated user
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse> setUserRole(
            @PathVariable String userId,
            @RequestParam User.Role role) {
        
        User updatedUser = userManagementService.setUserRole(userId, role);
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User role updated successfully")
                .data(updatedUser)
                .build());
    }
    
    /**
     * Assign role to user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    @PutMapping("/{userId}/roles/assign")
    public ResponseEntity<ApiResponse> assignRoleToUser(
            @PathVariable String userId,
            @RequestParam String roleId) {
        
        User updatedUser = userRoleService.assignRolesToUser(userId, roleId);
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Role assigned to user successfully")
                .data(updatedUser)
                .build());
    }
    
    /**
     * Add role to user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    @PutMapping("/{userId}/roles/add")
    public ResponseEntity<ApiResponse> addRoleToUser(
            @PathVariable String userId,
            @RequestParam String roleId) {
        
        User updatedUser = userRoleService.addRolesToUser(userId, roleId);
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Role added to user successfully")
                .data(updatedUser)
                .build());
    }
    
    /**
     * Remove role from user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    @PutMapping("/{userId}/roles/remove")
    public ResponseEntity<ApiResponse> removeRoleFromUser(
            @PathVariable String userId,
            @RequestParam String roleId) {
        
        User updatedUser = userRoleService.removeRolesFromUser(userId, roleId);
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Role removed from user successfully")
                .data(updatedUser)
                .build());
    }
}
