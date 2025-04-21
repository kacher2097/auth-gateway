package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.mongo.Role;
import com.authenhub.entity.mongo.User;
import com.authenhub.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ApiResponse> getUserRoles(@PathVariable String userId) {
        Role role = userRoleService.getUserRoles(userId);
//        Set<RoleDto.Response> roleResponses = roles.stream()
//                .map(RoleDto.Response::fromEntity)
//                .collect(Collectors.toSet());
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User roles retrieved successfully")
                .data(role)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ApiResponse> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody String roleId) {
        User user = userRoleService.assignRolesToUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Roles assigned to user successfully")
                .data(user)
                .build());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ApiResponse> addRolesToUser(
            @PathVariable String userId,
            @RequestBody String roleId) {
        User user = userRoleService.addRolesToUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Roles added to user successfully")
                .data(user)
                .build());
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ApiResponse> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody String roleId) {
        User user = userRoleService.removeRolesFromUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Roles removed from user successfully")
                .data(user)
                .build());
    }
}
