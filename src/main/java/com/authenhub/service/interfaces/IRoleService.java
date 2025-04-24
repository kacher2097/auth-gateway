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
    RoleResponse getRoleById(String id);

    /**
     * Get role with permissions
     *
     * @param id role id
     * @return role with permissions
     */
    RoleDetailedResponse getRoleWithPermissions(String id);

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
    RoleResponse updateRole(String id, RoleRequest request);

    /**
     * Delete a role
     *
     * @param id role id
     */
    void deleteRole(String id);

    /**
     * Add permissions to a role
     *
     * @param roleId role id
     * @param permissionIds permission ids
     * @return updated role
     */
    RoleResponse addPermissionsToRole(String roleId, Set<String> permissionIds);

    /**
     * Remove permissions from a role
     *
     * @param roleId role id
     * @param permissionIds permission ids
     * @return updated role
     */
    RoleResponse removePermissionsFromRole(String roleId, Set<String> permissionIds);

    /**
     * Validate permission ids
     *
     * @param permissionIds permission ids
     */
    void validatePermissionIds(Set<String> permissionIds);
}
