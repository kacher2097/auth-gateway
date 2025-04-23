package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.PermissionDto;
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
        List<PermissionDto.Response> permissions = permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ApiResponse<?> getPermissionById(@PathVariable String id) {
        PermissionDto.Response permission = permissionService.getPermissionById(id);
        return ApiResponse.success(permission);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ApiResponse<?> getPermissionsByCategory(@PathVariable String category) {
        List<PermissionDto.Response> permissions = permissionService.getPermissionsByCategory(category);
        return ApiResponse.success(permissions);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ApiResponse<?> createPermission(@Valid @RequestBody PermissionDto.Request request) {
        PermissionDto.Response permission = permissionService.createPermission(request);
        return ApiResponse.success(permission);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ApiResponse<?> updatePermission(
            @PathVariable String id,
            @Valid @RequestBody PermissionDto.Request request) {
        PermissionDto.Response permission = permissionService.updatePermission(id, request);
        return ApiResponse.success(permission);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ApiResponse<?> deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return ApiResponse.success(null);
    }
}
