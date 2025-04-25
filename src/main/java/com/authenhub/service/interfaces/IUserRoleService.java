package com.authenhub.service.interfaces;

import com.authenhub.entity.User;
import com.authenhub.entity.Role;

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
    User assignRolesToUser(Long userId, Long roleId);
    
    /**
     * Add roles to user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    User addRolesToUser(Long userId, Long roleId);
    
    /**
     * Remove roles from user
     *
     * @param userId user id
     * @param roleId role id
     * @return updated user
     */
    User removeRolesFromUser(Long userId, Long roleId);
    
    /**
     * Get user roles
     *
     * @param userId user id
     * @return user roles
     */
    Role getUserRoles(Long userId);
    
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
    void validateRoleIds(Long roleId);
}
