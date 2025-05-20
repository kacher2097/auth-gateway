package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.entity.Role;
import com.authenhub.entity.User;
import com.authenhub.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/roles")
@RequiredArgsConstructor
@Tag(name = "User Roles", description = "API để quản lý vai trò của người dùng")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    @Operation(summary = "Lấy vai trò của người dùng",
            description = "Lấy thông tin về vai trò của người dùng dựa trên ID người dùng.")
    public ApiResponse<?> getUserRoles(@PathVariable Long userId) {
        Role role = userRoleService.getUserRoles(userId);
        return ApiResponse.success(role);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "Gán vai trò cho người dùng",
            description = "Gán vai trò cho người dùng dựa trên ID người dùng và ID vai trò. Thay thế vai trò hiện tại.")
    public ApiResponse<?> assignRolesToUser(
            @PathVariable Long userId,
            @RequestBody Long roleId) {
        User user = userRoleService.assignRolesToUser(userId, roleId);
        return ApiResponse.success(user);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "Thêm vai trò cho người dùng",
            description = "Thêm vai trò mới cho người dùng dựa trên ID người dùng và ID vai trò. Giữ nguyên các vai trò hiện tại.")
    public ApiResponse<?> addRolesToUser(
            @PathVariable Long userId,
            @RequestBody Long roleId) {
        User user = userRoleService.addRolesToUser(userId, roleId);
        return ApiResponse.success(user);
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "Xóa vai trò của người dùng",
            description = "Xóa vai trò của người dùng dựa trên ID người dùng và ID vai trò.")
    public ApiResponse<?> removeRolesFromUser(
            @PathVariable Long userId,
            @RequestBody Long roleId) {
        User user = userRoleService.removeRolesFromUser(userId, roleId);
        return ApiResponse.success(user);
    }
}
