package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.dto.PermissionDto;
import com.authenhub.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse> getAllPermissions() {
        List<PermissionDto.Response> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Permissions retrieved successfully")
                .data(permissions)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<ApiResponse> getPermissionById(@PathVariable String id) {
        PermissionDto.Response permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Permission retrieved successfully")
                .data(permission)
                .build());
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<ApiResponse> getPermissionsByCategory(@PathVariable String category) {
        List<PermissionDto.Response> permissions = permissionService.getPermissionsByCategory(category);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Permissions retrieved successfully")
                .data(permissions)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<ApiResponse> createPermission(@Valid @RequestBody PermissionDto.Request request) {
        PermissionDto.Response permission = permissionService.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Permission created successfully")
                .data(permission)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<ApiResponse> updatePermission(
            @PathVariable String id,
            @Valid @RequestBody PermissionDto.Request request) {
        PermissionDto.Response permission = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Permission updated successfully")
                .data(permission)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<ApiResponse> deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Permission deleted successfully")
                .build());
    }
}
