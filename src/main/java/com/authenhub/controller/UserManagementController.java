package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.constant.enums.ApiResponseCode;
import com.authenhub.entity.User;
import com.authenhub.service.UserContext;
import com.authenhub.service.UserRoleService;
import com.authenhub.service.interfaces.IUserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

/**
 * Controller for user management operations
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserContext userContext;
    private final UserRoleService userRoleService;
    private final IUserManagementService userManagementService;

    /**
     * Get the current user's profile
     *
     * @return the current user's profile
     */
    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser() {
        User currentUser = userContext.getCurrentUser();

        if (currentUser == null) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, "User not authenticated");
        }

        return ApiResponse.success(currentUser);
    }

    /**
     * Update the current user's profile
     *
     * @param request update request
     * @return updated user
     */
    @PutMapping("/me")
    public ApiResponse<?> updateCurrentUser(@RequestBody UserUpdateRequest request) {
        User currentUser = userContext.getCurrentUser();

        if (currentUser == null) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, "User not authenticated");
        }

        try {
            User updatedUser = userManagementService.updateUser(currentUser.getId(), request);
            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user profile", e);
            return ApiResponse.error("500", "Error updating user profile: " + e.getMessage());
        }
    }

    /**
     * Get a user by ID
     *
     * @param userId user ID
     * @return user
     */
    @GetMapping("/{userId}")
    public ApiResponse<?> getUserById(@PathVariable String userId) {
        // Check if user is authenticated
        if (!userContext.isAuthenticated()) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, "User not authenticated");
        }

        // Only admins or the user themselves can view user details
        User currentUser = userContext.getCurrentUser();
        if (!userContext.isAdmin()) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, "You don't have permission to view this user");
        }

        try {
            User user = userManagementService.getCurrentUser();
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("Error retrieving user", e);
            return ApiResponse.error("500", "Error retrieving user: " + e.getMessage());
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
    public ApiResponse<?> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        try {
            User updatedUser = userManagementService.updateUser(userId, request);
            return ApiResponse.success(updatedUser);
        } catch (AccessDeniedException e) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error updating user", e);
            return ApiResponse.error("500", "Error updating user: " + e.getMessage());
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
    public ApiResponse<?> setUserActiveStatus(@PathVariable Long userId, @RequestParam boolean active) {
        try {
            User updatedUser = userManagementService.setUserActiveStatus(userId, active);
            return ApiResponse.success(updatedUser);
        } catch (AccessDeniedException e) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error updating user active status", e);
            return ApiResponse.error("500", "Error updating user active status: " + e.getMessage());
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
    public ApiResponse<?> setUserRole(@PathVariable Long userId, @RequestParam String role) {
        try {
            User updatedUser = userManagementService.setUserRole(userId, role);
            return ApiResponse.success(updatedUser);
        } catch (AccessDeniedException e) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Error updating user role", e);
            return ApiResponse.error("500", "Error updating user role: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/roles/assign")
    public ApiResponse<?> assignRoleToUser(
            @PathVariable Long userId,
            @RequestParam Long roleId) {

        User updatedUser = userRoleService.assignRolesToUser(userId, roleId);
        return ApiResponse.success(updatedUser);
    }

    /**
     * Add role to user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    @PutMapping("/{userId}/roles/add")
    public ApiResponse<?> addRoleToUser(
            @PathVariable Long userId,
            @RequestParam Long roleId) {

        User updatedUser = userRoleService.addRolesToUser(userId, roleId);
        return ApiResponse.success(updatedUser);
    }

    /**
     * Remove role from user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    @PutMapping("/{userId}/roles/remove")
    public ApiResponse<?> removeRoleFromUser(
            @PathVariable Long userId,
            @RequestParam Long roleId) {

        User updatedUser = userRoleService.removeRolesFromUser(userId, roleId);
        return ApiResponse.success(updatedUser);
    }
}
