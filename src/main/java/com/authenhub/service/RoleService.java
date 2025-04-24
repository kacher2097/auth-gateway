package com.authenhub.service;

import com.authenhub.bean.permission.PermissionResponse;
import com.authenhub.bean.permission.RoleDetailedResponse;
import com.authenhub.bean.permission.RoleRequest;
import com.authenhub.bean.permission.RoleResponse;
import com.authenhub.entity.mongo.Permission;
import com.authenhub.entity.mongo.Role;
import com.authenhub.exception.ResourceAlreadyExistsException;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.adapter.PermissionRepositoryAdapter;
import com.authenhub.repository.adapter.RoleRepositoryAdapter;
import com.authenhub.service.interfaces.IRoleService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepositoryAdapter roleRepository;
    private final PermissionRepositoryAdapter permissionRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return RoleResponse.fromEntity(role);
    }

    @Override
    public RoleDetailedResponse getRoleWithPermissions(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        Set<Permission> permissions = role.getPermissionIds().stream()
                .map(permId -> permissionRepository.findById(permId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permId)))
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
                .permissionIds(request.getPermissions())
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();

        Role savedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(savedRole);
    }

    @Override
    public RoleResponse updateRole(String id, RoleRequest request) {
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
        role.setPermissionIds(request.getPermissions());
        role.setUpdatedAt(TimestampUtils.now());

        Role updatedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(updatedRole);
    }

    @Override
    public void deleteRole(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Prevent deletion of system roles
        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be deleted");
        }

        roleRepository.delete(role);
    }

    @Override
    public RoleResponse addPermissionsToRole(String roleId, Set<String> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        // Prevent modification of system roles
        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be modified");
        }

        // Validate that all permission IDs exist
        validatePermissionIds(permissionIds);

        // Add new permissions
        Set<String> updatedPermissions = new HashSet<>(role.getPermissionIds());
        updatedPermissions.addAll(permissionIds);
        role.setPermissionIds(updatedPermissions);
        role.setUpdatedAt(TimestampUtils.now());

        Role updatedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(updatedRole);
    }

    @Override
    public RoleResponse removePermissionsFromRole(String roleId, Set<String> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        // Prevent modification of system roles
        if (role.isSystem()) {
            throw new IllegalStateException("System roles cannot be modified");
        }

        // Remove permissions
        Set<String> updatedPermissions = new HashSet<>(role.getPermissionIds());
        updatedPermissions.removeAll(permissionIds);
        role.setPermissionIds(updatedPermissions);
        role.setUpdatedAt(TimestampUtils.now());

        Role updatedRole = roleRepository.save(role);
        return RoleResponse.fromEntity(updatedRole);
    }

    @Override
    public void validatePermissionIds(Set<String> permissionIds) {
        for (String permissionId : permissionIds) {
            if (!permissionRepository.existsByName(permissionId)) {
                throw new ResourceNotFoundException("Permission not found with id: " + permissionId);
            }
        }
    }
}
