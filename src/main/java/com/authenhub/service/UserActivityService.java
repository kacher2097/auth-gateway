package com.authenhub.service;

import com.authenhub.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for tracking user activities
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserContext userContext;
    
    /**
     * Log an activity for the current user
     *
     * @param activity the activity to log
     * @return true if logged successfully, false otherwise
     */
    public boolean logActivity(String activity) {
        User currentUser = userContext.getCurrentUser();
        
        if (currentUser == null) {
            log.warn("Cannot log activity: No authenticated user");
            return false;
        }
        
        // Here you would typically save the activity to a database
        log.info("User activity: {} performed by user ID: {}, username: {}", 
                activity, currentUser.getId(), currentUser.getUsername());
        
        return true;
    }
    
    /**
     * Check if the current user can perform an action
     *
     * @param action the action to check
     * @return true if allowed, false otherwise
     */
    public boolean canPerformAction(String action) {
        // Check if user is authenticated
        if (!userContext.isAuthenticated()) {
            return false;
        }
        
        // Admin can do anything
        if (userContext.isAdmin()) {
            return true;
        }
        
        // Check specific permissions
        return userContext.hasPermission(action);
    }
}
