package com.authenhub.service;

import com.authenhub.entity.mongo.User;
import com.authenhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for managing and accessing the current authenticated user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserContext {

    private final UserRepository userRepository;
    
    /**
     * Get the current authenticated user
     *
     * @return the current user or null if not authenticated
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        
        // If the principal is already a User object, return it directly
        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        
        // Otherwise, get the username and load the user from the repository
        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            log.debug("User not found for username: {}", username);
            return null;
        }
        
        return userOptional.get();
    }
    
    /**
     * Get the current user's ID
     *
     * @return the current user's ID or null if not authenticated
     */
    public String getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * Get the current user's username
     *
     * @return the current username or null if not authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        
        return authentication.getName();
    }
    
    /**
     * Check if the current user has a specific role
     *
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
    
    /**
     * Check if the current user has a specific permission
     *
     * @param permission the permission to check
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }
    
    /**
     * Check if the current user is authenticated
     *
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    /**
     * Check if the current user is an admin
     *
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && User.Role.ADMIN.equals(user.getRole());
    }
}
