package com.authenhub.security;

import com.authenhub.entity.User;
import com.authenhub.service.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Security utility for user-related operations
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final UserContext userContext;
    
    /**
     * Check if the current user can access a specific user's data
     *
     * @param userId the user ID to check access for
     * @return true if allowed, false otherwise
     */
    public boolean canAccessUser(String userId) {
        // Admin can access any user
        if (userContext.isAdmin()) {
            return true;
        }
        
        // Users can only access their own data
        User currentUser = userContext.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return currentUser.getId().equals(userId);
    }
    
    /**
     * Check if the current user can modify a specific user's data
     *
     * @param userId the user ID to check modification rights for
     * @return true if allowed, false otherwise
     */
    public boolean canModifyUser(Long userId) {
        // Admin can modify any user
        if (userContext.isAdmin()) {
            return true;
        }
        
        // Users can only modify their own data
        User currentUser = userContext.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return currentUser.getId().equals(userId);
    }
    
    /**
     * Check if the current user can delete a specific user
     *
     * @param userId the user ID to check deletion rights for
     * @return true if allowed, false otherwise
     */
    public boolean canDeleteUser(String userId) {
        // Only admin can delete users
        if (!userContext.isAdmin()) {
            return false;
        }
        
        // Admin cannot delete themselves
        User currentUser = userContext.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return !currentUser.getId().equals(userId);
    }
}
