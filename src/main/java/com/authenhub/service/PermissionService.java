package com.authenhub.service;

import com.authenhub.dto.PermissionDto;
import com.authenhub.entity.Permission;
import com.authenhub.exception.ResourceAlreadyExistsException;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.PermissionRepository;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionDto.Response> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public PermissionDto.Response getPermissionById(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        return PermissionDto.Response.fromEntity(permission);
    }

    public List<PermissionDto.Response> getPermissionsByCategory(String category) {
        return permissionRepository.findByCategory(category).stream()
                .map(PermissionDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public PermissionDto.Response createPermission(PermissionDto.Request request) {
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
        return PermissionDto.Response.fromEntity(savedPermission);
    }

    public PermissionDto.Response updatePermission(String id, PermissionDto.Request request) {
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
        return PermissionDto.Response.fromEntity(updatedPermission);
    }

    public void deletePermission(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        
        permissionRepository.delete(permission);
    }
}
