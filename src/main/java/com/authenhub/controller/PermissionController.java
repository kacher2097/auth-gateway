package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.permission.PermissionRequest;
import com.authenhub.bean.permission.PermissionResponse;
import com.authenhub.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/permissions")
@Tag(name = "Permissions", description = "API để quản lý quyền trong hệ thống")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
//    @PreAuthorize("hasAuthority('permission:read')")
    @Operation(summary = "Lấy danh sách tất cả quyền",
            description = "Trả về danh sách tất cả quyền trong hệ thống.")
    public ApiResponse<?> getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('permission:read')")
    @Operation(summary = "Lấy quyền theo ID",
            description = "Trả về thông tin chi tiết của quyền dựa trên ID.")
    public ApiResponse<?> getPermissionById(@PathVariable Long id) {
        PermissionResponse permission = permissionService.getPermissionById(id);
        return ApiResponse.success(permission);
    }

    @GetMapping("/category/{category}")
//    @PreAuthorize("hasAuthority('permission:read')")
    @Operation(summary = "Lấy quyền theo danh mục",
            description = "Trả về danh sách quyền thuộc một danh mục cụ thể.")
    public ApiResponse<?> getPermissionsByCategory(@PathVariable String category) {
        List<PermissionResponse> permissions = permissionService.getPermissionsByCategory(category);
        return ApiResponse.success(permissions);
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('permission:create')")
    @Operation(summary = "Tạo quyền mới",
            description = "Tạo một quyền mới trong hệ thống.")
    public ApiResponse<?> createPermission(@Valid @RequestBody PermissionRequest request) {
        PermissionResponse permission = permissionService.createPermission(request);
        return ApiResponse.success(permission);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('permission:update')")
    @Operation(summary = "Cập nhật quyền",
            description = "Cập nhật thông tin của quyền dựa trên ID.")
    public ApiResponse<?> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        PermissionResponse permission = permissionService.updatePermission(id, request);
        return ApiResponse.success(permission);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('permission:delete')")
    @Operation(summary = "Xóa quyền",
            description = "Xóa quyền dựa trên ID.")
    public ApiResponse<?> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success(null);
    }
}
