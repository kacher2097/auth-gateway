package com.authenhub.service.interfaces;

import com.authenhub.dto.RoleDto;

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
    List<RoleDto.Response> getAllRoles();
    
    /**
     * Get role by id
     *
     * @param id role id
     * @return role
     */
    RoleDto.Response getRoleById(String id);
    
    /**
     * Get role with permissions
     *
     * @param id role id
     * @return role with permissions
     */
    RoleDto.DetailedResponse getRoleWithPermissions(String id);
    
    /**
     * Create a new role
     *
     * @param request role request
     * @return created role
     */
    RoleDto.Response createRole(RoleDto.Request request);
    
    /**
     * Update a role
     *
     * @param id role id
     * @param request role request
     * @return updated role
     */
    RoleDto.Response updateRole(String id, RoleDto.Request request);
    
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
    RoleDto.Response addPermissionsToRole(String roleId, Set<String> permissionIds);
    
    /**
     * Remove permissions from a role
     *
     * @param roleId role id
     * @param permissionIds permission ids
     * @return updated role
     */
    RoleDto.Response removePermissionsFromRole(String roleId, Set<String> permissionIds);
    
    /**
     * Validate permission ids
     *
     * @param permissionIds permission ids
     */
    void validatePermissionIds(Set<String> permissionIds);
}
