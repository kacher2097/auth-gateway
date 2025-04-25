package com.authenhub.service.interfaces;

import com.authenhub.bean.permission.RoleDetailedResponse;
import com.authenhub.bean.permission.RoleRequest;
import com.authenhub.bean.permission.RoleResponse;

import java.util.List;
import java.util.Set;

/**
 * Interface for role service operations
 */
public interface IRoleService {

    /**
     * Get all roles
     *
     * @return list of roles
     */
    List<RoleResponse> getAllRoles();

    /**
     * Get role by id
     *
     * @param id role id
     * @return role
     */
    RoleResponse getRoleById(Long id);

    /**
     * Get role with permissions
     *
     * @param id role id
     * @return role with permissions
     */
    RoleDetailedResponse getRoleWithPermissions(Long id);

    /**
     * Create a new role
     *
     * @param request role request
     * @return created role
     */
    RoleResponse createRole(RoleRequest request);

    /**
     * Update a role
     *
     * @param id role id
     * @param request role request
     * @return updated role
     */
    RoleResponse updateRole(Long id, RoleRequest request);

    /**
     * Delete a role
     *
     * @param id role id
     */
    void deleteRole(Long id);

    /**
     * Add permissions to a role
     *
     * @param roleId role id
     * @param permissionIds permission ids
     * @return updated role
     */
    RoleResponse addPermissionsToRole(Long roleId, Set<Long> permissionIds);

    /**
     * Remove permissions from a role
     *
     * @param roleId role id
     * @param permissionIds permission ids
     * @return updated role
     */
    RoleResponse removePermissionsFromRole(Long roleId, Set<Long> permissionIds);

    /**
     * Validate permission ids
     *
     * @param permissionIds permission ids
     */
    void validatePermissionIds(Set<Long> permissionIds);
}
