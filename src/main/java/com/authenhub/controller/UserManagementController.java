package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.constant.enums.ApiResponseCode;
import com.authenhub.entity.User;
import com.authenhub.service.UserContext;
import com.authenhub.service.UserRoleService;
import com.authenhub.service.interfaces.IUserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "API quản lý người dùng: xem và cập nhật thông tin cá nhân")
public class UserManagementController {

    private final UserContext userContext;
    private final UserRoleService userRoleService;
    private final IUserManagementService userManagementService;

    @GetMapping("/me")
    @Operation(summary = "Lấy thông tin người dùng hiện tại",
            description = "Lấy thông tin của người dùng đã đăng nhập.")
    public ApiResponse<?> getCurrentUser() {
        User currentUser = userContext.getCurrentUser();

        if (currentUser == null) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, "User not authenticated");
        }

        return ApiResponse.success(currentUser);
    }

    @PutMapping("/me")
    @Operation(summary = "Cập nhật thông tin người dùng hiện tại",
            description = "Cập nhật thông tin của người dùng đã đăng nhập.")
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

    @GetMapping("/{userId}")
    @Operation(summary = "Lấy thông tin người dùng theo ID",
            description = "Lấy thông tin của người dùng dựa trên ID. Chỉ admin mới có quyền truy cập.")
    public ApiResponse<?> getUserById(@PathVariable String userId) {
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

    @PutMapping("/{userId}")
    @Operation(summary = "Cập nhật thông tin người dùng theo ID",
            description = "Cập nhật thông tin của người dùng dựa trên ID. Chỉ admin mới có quyền truy cập.")
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

    @PutMapping("/{userId}/active")
    @Operation(summary = "Kích hoạt tài khoản người dùng",
            description = "Kích hoạt tài khoản của người dùng dựa trên ID.")
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

    @PutMapping("/{userId}/roles/add")
    public ApiResponse<?> addRoleToUser(
            @PathVariable Long userId,
            @RequestParam Long roleId) {

        User updatedUser = userRoleService.addRolesToUser(userId, roleId);
        return ApiResponse.success(updatedUser);
    }

    @PutMapping("/{userId}/roles/remove")
    public ApiResponse<?> removeRoleFromUser(
            @PathVariable Long userId,
            @RequestParam Long roleId) {

        User updatedUser = userRoleService.removeRolesFromUser(userId, roleId);
        return ApiResponse.success(updatedUser);
    }
}
