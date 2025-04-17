package com.authenhub.service;

import com.authenhub.dto.PermissionDto;
import com.authenhub.dto.RoleDto;
import com.authenhub.entity.Permission;
import com.authenhub.entity.Role;
import com.authenhub.exception.ResourceAlreadyExistsException;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.PermissionRepository;
import com.authenhub.repository.RoleRepository;
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

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<RoleDto.Response> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto.Response getRoleById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return RoleDto.Response.fromEntity(role);
    }

    @Override
    public RoleDto.DetailedResponse getRoleWithPermissions(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        Set<Permission> permissions = role.getPermissionIds().stream()
                .map(permId -> permissionRepository.findById(permId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permId)))
                .collect(Collectors.toSet());

        Set<PermissionDto.Response> permissionResponses = permissions.stream()
                .map(PermissionDto.Response::fromEntity)
                .collect(Collectors.toSet());

        return RoleDto.DetailedResponse.builder()
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
    public RoleDto.Response createRole(RoleDto.Request request) {
        // Check if role with same name already exists
        if (roleRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + request.getName());
        }

        // Validate that all permission IDs exist
        validatePermissionIds(request.getPermissionIds());

        Role role = Role.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .isSystem(false) // Only system can create system roles
                .permissionIds(request.getPermissionIds())
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();

        Role savedRole = roleRepository.save(role);
        return RoleDto.Response.fromEntity(savedRole);
    }

    @Override
    public RoleDto.Response updateRole(String id, RoleDto.Request request) {
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
        validatePermissionIds(request.getPermissionIds());

        role.setName(request.getName());
        role.setDisplayName(request.getDisplayName());
        role.setDescription(request.getDescription());
        role.setPermissionIds(request.getPermissionIds());
        role.setUpdatedAt(TimestampUtils.now());

        Role updatedRole = roleRepository.save(role);
        return RoleDto.Response.fromEntity(updatedRole);
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
    public RoleDto.Response addPermissionsToRole(String roleId, Set<String> permissionIds) {
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
        return RoleDto.Response.fromEntity(updatedRole);
    }

    @Override
    public RoleDto.Response removePermissionsFromRole(String roleId, Set<String> permissionIds) {
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
        return RoleDto.Response.fromEntity(updatedRole);
    }

    @Override
    public void validatePermissionIds(Set<String> permissionIds) {
        for (String permissionId : permissionIds) {
            if (!permissionRepository.existsById(permissionId)) {
                throw new ResourceNotFoundException("Permission not found with id: " + permissionId);
            }
        }
    }
}
