package com.authenhub.service;

import com.authenhub.bean.permission.PermissionRequest;
import com.authenhub.bean.permission.PermissionResponse;
import com.authenhub.entity.mongo.Permission;
import com.authenhub.exception.ResourceAlreadyExistsException;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.PermissionRepository;
import com.authenhub.service.interfaces.IPermissionService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse getPermissionById(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        return PermissionResponse.fromEntity(permission);
    }

    @Override
    public List<PermissionResponse> getPermissionsByCategory(String category) {
        return permissionRepository.findByCategory(category).stream()
                .map(PermissionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse createPermission(PermissionRequest request) {
        // Check if permission with same name already exists
        if (permissionRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Permission already exists with name: " + request.getName());
        }

        Permission permission = Permission.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .category(request.getCategory())
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();

        Permission savedPermission = permissionRepository.save(permission);
        return PermissionResponse.fromEntity(savedPermission);
    }

    @Override
    public PermissionResponse updatePermission(String id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        // Check if new name is already taken by another permission
        if (!permission.getName().equals(request.getName()) &&
                permissionRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Permission already exists with name: " + request.getName());
        }

        permission.setName(request.getName());
        permission.setDisplayName(request.getDisplayName());
        permission.setDescription(request.getDescription());
        permission.setCategory(request.getCategory());
        permission.setUpdatedAt(TimestampUtils.now());

        Permission updatedPermission = permissionRepository.save(permission);
        return PermissionResponse.fromEntity(updatedPermission);
    }

    @Override
    public void deletePermission(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        permissionRepository.delete(permission);
    }
}
