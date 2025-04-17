package com.authenhub.service.interfaces;

import com.authenhub.dto.PermissionDto;

import java.util.List;

/**
 * Interface for permission service operations
 */
public interface IPermissionService {
    
    /**
     * Get all permissions
     *
     * @return list of permissions
     */
    List<PermissionDto.Response> getAllPermissions();
    
    /**
     * Get permission by id
     *
     * @param id permission id
     * @return permission
     */
    PermissionDto.Response getPermissionById(String id);
    
    /**
     * Get permissions by category
     *
     * @param category permission category
     * @return list of permissions
     */
    List<PermissionDto.Response> getPermissionsByCategory(String category);
    
    /**
     * Create a new permission
     *
     * @param request permission request
     * @return created permission
     */
    PermissionDto.Response createPermission(PermissionDto.Request request);
    
    /**
     * Update a permission
     *
     * @param id permission id
     * @param request permission request
     * @return updated permission
     */
    PermissionDto.Response updatePermission(String id, PermissionDto.Request request);
    
    /**
     * Delete a permission
     *
     * @param id permission id
     */
    void deletePermission(String id);
}
