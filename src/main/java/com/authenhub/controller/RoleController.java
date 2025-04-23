package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.RoleDto;
import com.authenhub.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        List<RoleDto.Response> roles = roleService.getAllRoles();
        return ApiResponse.success(roles);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<?> getRoleById(@PathVariable String id) {
        RoleDto.Response role = roleService.getRoleById(id);
        return ApiResponse.success(role);
    }

    @GetMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<?> getRoleWithPermissions(@PathVariable String id) {
        RoleDto.DetailedResponse role = roleService.getRoleWithPermissions(id);
        return ApiResponse.success(role);
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('role:create')")
    public ApiResponse<?> createRole(@Valid @RequestBody RoleDto.Request request) {
        RoleDto.Response role = roleService.createRole(request);
        return ApiResponse.success(role);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:update')")
    public ApiResponse<?> updateRole(
            @PathVariable String id,
            @Valid @RequestBody RoleDto.Request request) {
        RoleDto.Response role = roleService.updateRole(id, request);
        return ApiResponse.success(role);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:delete')")
    public ApiResponse<?> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:update')")
    public ApiResponse<?> addPermissionsToRole(
            @PathVariable String id,
            @RequestBody Set<String> permissionIds) {
        RoleDto.Response role = roleService.addPermissionsToRole(id, permissionIds);
        return ApiResponse.success(role);
    }

    @DeleteMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:update')")
    public ApiResponse<?> removePermissionsFromRole(
            @PathVariable String id,
            @RequestBody Set<String> permissionIds) {
        RoleDto.Response role = roleService.removePermissionsFromRole(id, permissionIds);
        return ApiResponse.success(role);
    }
}
