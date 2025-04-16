package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<RoleDto.Response> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Roles retrieved successfully")
                .data(roles)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable String id) {
        RoleDto.Response role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Role retrieved successfully")
                .data(role)
                .build());
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse> getRoleWithPermissions(@PathVariable String id) {
        RoleDto.DetailedResponse role = roleService.getRoleWithPermissions(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Role with permissions retrieved successfully")
                .data(role)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RoleDto.Request request) {
        RoleDto.Response role = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Role created successfully")
                .data(role)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponse> updateRole(
            @PathVariable String id,
            @Valid @RequestBody RoleDto.Request request) {
        RoleDto.Response role = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Role updated successfully")
                .data(role)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Role deleted successfully")
                .build());
    }

    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponse> addPermissionsToRole(
            @PathVariable String id,
            @RequestBody Set<String> permissionIds) {
        RoleDto.Response role = roleService.addPermissionsToRole(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Permissions added to role successfully")
                .data(role)
                .build());
    }

    @DeleteMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponse> removePermissionsFromRole(
            @PathVariable String id,
            @RequestBody Set<String> permissionIds) {
        RoleDto.Response role = roleService.removePermissionsFromRole(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Permissions removed from role successfully")
                .data(role)
                .build());
    }
}
