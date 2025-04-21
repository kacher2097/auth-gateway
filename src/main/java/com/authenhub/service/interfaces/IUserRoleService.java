package com.authenhub.service.interfaces;

import com.authenhub.entity.mongo.Role;
import com.authenhub.entity.mongo.User;

/**
 * Interface for user role service operations
 */
public interface IUserRoleService {
    
    /**
     * Assign roles to user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    User assignRolesToUser(String userId, String roleId);
    
    /**
     * Add roles to user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    User addRolesToUser(String userId, String roleId);
    
    /**
     * Remove roles from user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    User removeRolesFromUser(String userId, String roleId);
    
    /**
     * Get user roles
     *
     * @param userId user id
     * @return user roles
     */
    Role getUserRoles(String userId);
    
    /**
     * Check if user has permission
     *
     * @param user user
     * @param permissionName permission name
     * @return true if user has permission
     */
    boolean hasPermission(User user, String permissionName);
    
    /**
     * Validate role ids
     *
     * @param roleId role id
     */
    void validateRoleIds(String roleId);
}
