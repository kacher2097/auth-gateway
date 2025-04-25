package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.entity.Role;
import com.authenhub.entity.User;
import com.authenhub.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ApiResponse<?> getUserRoles(@PathVariable Long userId) {
        Role role = userRoleService.getUserRoles(userId);
        return ApiResponse.success(role);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:update')")
    public ApiResponse<?> assignRolesToUser(
            @PathVariable Long userId,
            @RequestBody Long roleId) {
        User user = userRoleService.assignRolesToUser(userId, roleId);
        return ApiResponse.success(user);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:update')")
    public ApiResponse<?> addRolesToUser(
            @PathVariable Long userId,
            @RequestBody Long roleId) {
        User user = userRoleService.addRolesToUser(userId, roleId);
        return ApiResponse.success(user);
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('user:update')")
    public ApiResponse<?> removeRolesFromUser(
            @PathVariable Long userId,
            @RequestBody Long roleId) {
        User user = userRoleService.removeRolesFromUser(userId, roleId);
        return ApiResponse.success(user);
    }
}
