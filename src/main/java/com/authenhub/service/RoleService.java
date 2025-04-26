package com.authenhub.service;

import com.authenhub.bean.permission.PermissionResponse;
import com.authenhub.bean.permission.RoleDetailedResponse;
import com.authenhub.bean.permission.RoleRequest;
import com.authenhub.bean.permission.RoleResponse;
import com.authenhub.entity.Permission;
import com.authenhub.entity.Role;
import com.authenhub.entity.RolePermission;
import com.authenhub.exception.ResourceAlreadyExistsException;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.jpa.PermissionJpaRepository;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.RolePermissionRepository;
import com.authenhub.service.interfaces.IRoleService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleJpaRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        RoleResponse roleResponse = RoleResponse.fromEntity(role);
        List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleId(id);
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            log.info("RolePermission not found for id: {}", id);
            return null;
        }

        Set<String> lstFunctionAction = rolePermissions.stream()
                .map(RolePermission::getPermissionName)
                .collect(Collectors.toSet());
        roleResponse.setPermissions(lstFunctionAction);
        return roleResponse;
    }

    @Override
    public RoleDetailedResponse getRoleWithPermissions(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleId(id);

        if (rolePermissions.isEmpty()) {
            return RoleDetailedResponse.builder()
                    .id(role.getId())
                    .name(role.getName())
                    .displayName(role.getDisplayName())
                    .description(role.getDescription())
                    .isSystem(role.isSystem())
                    .permissions(new HashSet<>())
                    .createdAt(role.getCreatedAt())
                    .updatedAt(role.getUpdatedAt())
                    .build();
        }

        Set<Permission> permissions = rolePermissions.stream()
                .map(rolePermission -> permissionRepository.findByName(rolePermission.getPermissionName())
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + rolePermission.getRoleId())))
                .collect(Collectors.toSet());

        Set<PermissionResponse> permissionResponses = permissions.stream()
                .map(PermissionResponse::fromEntity)
                .collect(Collectors.toSet());

        return RoleDetailedResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .isSystem(role.isSystem())
                .permissions(permissionResponses)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    @Override
    public RoleResponse createRole(RoleRequest request) {
        // Check if role with same name already exists
        if (roleRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + request.getName());
        }

        // Validate that all permission IDs exist
        validatePermissionIds(request.getPermissions());

        Role role = Role.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .isSystem(false) // Only system can create system roles
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();

        Role savedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Prevent modification of system roles
        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be modified");
        }

        // Check if new name is already taken by another role
        if (!role.getName().equals(request.getName()) &&
                roleRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + request.getName());
        }

        // Validate that all permission IDs exist
        validatePermissionIds(request.getPermissions());

        role.setName(request.getName());
        role.setDisplayName(request.getDisplayName());
        role.setDescription(request.getDescription());
        role.setUpdatedAt(TimestampUtils.now());

        Role updatedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Prevent deletion of system roles
        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be deleted");
        }

        roleRepository.delete(role);
    }

    @Override
    public RoleResponse addPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        // Prevent modification of system roles
        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be modified");
        }

        // Validate that all permission IDs exist
        validatePermissionIds(permissionIds);

        // Add new permissions
//        RolePermission rolePermission = RolePermission.builder()
//                .roleId(roleId)
//                .permissionId(permissionIds)
//                .build();

//        Set<Long> updatedPermissions = new HashSet<>(role.getPermissionIds());
//        updatedPermissions.addAll(permissionIds);
//        role.setPermissionIds(updatedPermissions);
        role.setUpdatedAt(TimestampUtils.now());

        Role updatedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(updatedRole);
    }

    @Override
    public RoleResponse removePermissionsFromRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        // Prevent modification of system roles
        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be modified");
        }

        // Remove permissions
//        Set<Long> updatedPermissions = new HashSet<>(role.getPermissionIds());
//        updatedPermissions.removeAll(permissionIds);
//        role.setPermissionIds(updatedPermissions);
        role.setUpdatedAt(TimestampUtils.now());

        Role updatedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(updatedRole);
    }

    @Override
    public void validatePermissionIds(Set<Long> permissionIds) {
        for (Long permissionId : permissionIds) {
            if (!permissionRepository.existsById(permissionId)) {
                throw new ResourceNotFoundException("Permission not found with id: " + permissionId);
            }
        }
    }
}
