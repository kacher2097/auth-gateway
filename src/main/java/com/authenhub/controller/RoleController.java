package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.permission.RoleDetailedResponse;
import com.authenhub.bean.permission.RoleRequest;
import com.authenhub.bean.permission.RoleResponse;
import com.authenhub.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API để quản lý vai trò và phân quyền")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
//    @PreAuthorize("hasAuthority('role:read')")
    @Operation(summary = "Lấy danh sách tất cả vai trò",
            description = "Trả về danh sách tất cả vai trò trong hệ thống.")
    public ApiResponse<?> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ApiResponse.success(roles);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:read')")
    @Operation(summary = "Lấy vai trò theo ID",
            description = "Trả về thông tin chi tiết của vai trò dựa trên ID.")
    public ApiResponse<?> getRoleById(@PathVariable Long id) {
        RoleResponse role = roleService.getRoleById(id);
        return ApiResponse.success(role);
    }

    @GetMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:read')")
    @Operation(summary = "Lấy vai trò và quyền theo ID",
            description = "Trả về thông tin chi tiết của vai trò và các quyền liên kết dựa trên ID.")
    public ApiResponse<?> getRoleWithPermissions(@PathVariable Long id) {
        RoleDetailedResponse role = roleService.getRoleWithPermissions(id);
        return ApiResponse.success(role);
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('role:create')")
    @Operation(summary = "Tạo vai trò mới",
            description = "Tạo một vai trò mới trong hệ thống.")
    public ApiResponse<?> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ApiResponse.success(role);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:update')")
    @Operation(summary = "Cập nhật vai trò",
            description = "Cập nhật thông tin của vai trò dựa trên ID.")
    public ApiResponse<?> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.updateRole(id, request);
        return ApiResponse.success(role);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:delete')")
    @Operation(summary = "Xóa vai trò",
            description = "Xóa vai trò dựa trên ID.")
    public ApiResponse<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:update')")
    @Operation(summary = "Thêm quyền cho vai trò",
            description = "Thêm các quyền cho vai trò dựa trên ID vai trò và danh sách ID quyền.")
    public ApiResponse<?> addPermissionsToRole(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        RoleResponse role = roleService.addPermissionsToRole(id, permissionIds);
        return ApiResponse.success(role);
    }

    @DeleteMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:update')")
    @Operation(summary = "Xóa quyền khỏi vai trò",
            description = "Xóa các quyền khỏi vai trò dựa trên ID vai trò và danh sách ID quyền.")
    public ApiResponse<?> removePermissionsFromRole(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        RoleResponse role = roleService.removePermissionsFromRole(id, permissionIds);
        return ApiResponse.success(role);
    }
}
