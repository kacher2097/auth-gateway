package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.permission.PermissionRequest;
import com.authenhub.bean.permission.PermissionResponse;
import com.authenhub.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ApiResponse<?> getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ApiResponse<?> getPermissionById(@PathVariable Long id) {
        PermissionResponse permission = permissionService.getPermissionById(id);
        return ApiResponse.success(permission);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ApiResponse<?> getPermissionsByCategory(@PathVariable String category) {
        List<PermissionResponse> permissions = permissionService.getPermissionsByCategory(category);
        return ApiResponse.success(permissions);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ApiResponse<?> createPermission(@Valid @RequestBody PermissionRequest request) {
        PermissionResponse permission = permissionService.createPermission(request);
        return ApiResponse.success(permission);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ApiResponse<?> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        PermissionResponse permission = permissionService.updatePermission(id, request);
        return ApiResponse.success(permission);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ApiResponse<?> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success(null);
    }
}
