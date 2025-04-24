package com.authenhub.service.interfaces;

import com.authenhub.bean.permission.PermissionRequest;
import com.authenhub.bean.permission.PermissionResponse;

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
    List<PermissionResponse> getAllPermissions();

    /**
     * Get permission by id
     *
     * @param id permission id
     * @return permission
     */
    PermissionResponse getPermissionById(String id);

    /**
     * Get permissions by category
     *
     * @param category permission category
     * @return list of permissions
     */
    List<PermissionResponse> getPermissionsByCategory(String category);

    /**
     * Create a new permission
     *
     * @param request permission request
     * @return created permission
     */
    PermissionResponse createPermission(PermissionRequest request);

    /**
     * Update a permission
     *
     * @param id permission id
     * @param request permission request
     * @return updated permission
     */
    PermissionResponse updatePermission(String id, PermissionRequest request);

    /**
     * Delete a permission
     *
     * @param id permission id
     */
    void deletePermission(String id);
}
