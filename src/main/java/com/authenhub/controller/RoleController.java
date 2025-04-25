package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.permission.RoleDetailedResponse;
import com.authenhub.bean.permission.RoleRequest;
import com.authenhub.bean.permission.RoleResponse;
import com.authenhub.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
//    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<?> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ApiResponse.success(roles);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<?> getRoleById(@PathVariable Long id) {
        RoleResponse role = roleService.getRoleById(id);
        return ApiResponse.success(role);
    }

    @GetMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<?> getRoleWithPermissions(@PathVariable Long id) {
        RoleDetailedResponse role = roleService.getRoleWithPermissions(id);
        return ApiResponse.success(role);
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('role:create')")
    public ApiResponse<?> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ApiResponse.success(role);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:update')")
    public ApiResponse<?> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        RoleResponse role = roleService.updateRole(id, request);
        return ApiResponse.success(role);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:delete')")
    public ApiResponse<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:update')")
    public ApiResponse<?> addPermissionsToRole(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        RoleResponse role = roleService.addPermissionsToRole(id, permissionIds);
        return ApiResponse.success(role);
    }

    @DeleteMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:update')")
    public ApiResponse<?> removePermissionsFromRole(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        RoleResponse role = roleService.removePermissionsFromRole(id, permissionIds);
        return ApiResponse.success(role);
    }
}
