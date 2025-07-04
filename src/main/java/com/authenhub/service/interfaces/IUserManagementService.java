package com.authenhub.service.interfaces;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.entity.User;

/**
 * Interface for user management service operations
 */
public interface IUserManagementService {

    /**
     * Update user
     *
     * @param userId user id
     * @param request user update request
     * @return updated user
     */
    User updateUser(Long userId, UserUpdateRequest request);

    /**
     * Set user active status
     *
     * @param userId user id
     * @param active active status
     * @return updated user
     */
    User setUserActiveStatus(Long userId, boolean active);

    /**
     * Set user role
     *
     * @param userId user id
     * @param role user role
     * @return updated user
     */
    User setUserRole(Long userId, String role);

    /**
     * Get the current authenticated user
     *
     * @return the current user or null if not authenticated
     */
    User getCurrentUser();
}
