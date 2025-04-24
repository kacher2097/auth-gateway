package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.entity.mongo.Role;
import com.authenhub.entity.mongo.User;
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
    public ApiResponse<?> getUserRoles(@PathVariable String userId) {
        Role role = userRoleService.getUserRoles(userId);
        return ApiResponse.success(role);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:update')")
    public ApiResponse<?> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody String roleId) {
        User user = userRoleService.assignRolesToUser(userId, roleId);
        return ApiResponse.success(user);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:update')")
    public ApiResponse<?> addRolesToUser(
            @PathVariable String userId,
            @RequestBody String roleId) {
        User user = userRoleService.addRolesToUser(userId, roleId);
        return ApiResponse.success(user);
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('user:update')")
    public ApiResponse<?> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody String roleId) {
        User user = userRoleService.removeRolesFromUser(userId, roleId);
        return ApiResponse.success(user);
    }
}
