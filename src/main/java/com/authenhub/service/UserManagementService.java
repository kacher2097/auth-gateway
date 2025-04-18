package com.authenhub.service;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.entity.User;
import com.authenhub.repository.UserRepository;
import com.authenhub.security.UserSecurity;
import com.authenhub.service.interfaces.IUserManagementService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService implements IUserManagementService {

    private final UserRepository userRepository;
    private final UserContext userContext;
    private final UserSecurity userSecurity;
    private final UserActivityService userActivityService;

    @Override
    public User updateUser(String userId, UserUpdateRequest request) {
        // Check if the current user can modify this user
        if (!userSecurity.canModifyUser(userId)) {
            throw new AccessDeniedException("You don't have permission to update this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.debug("Updating user: {}", userId);

        // Update fields if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            // Check if email is already in use by another user
            if (userRepository.existsByEmail(request.getEmail()) &&
                !user.getEmail().equals(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        user.setUpdatedAt(TimestampUtils.now());
        User updatedUser = userRepository.save(user);

        // Log the activity
        userActivityService.logActivity("Updated user profile for user ID: " + userId);

        return updatedUser;
    }

    @Override
    public User setUserActiveStatus(String userId, boolean active) {
        // Only admins can change user active status
        if (!userContext.isAdmin()) {
            throw new AccessDeniedException("Only administrators can change user active status");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Don't allow deactivating your own account
        User currentUser = userContext.getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(userId) && !active) {
            throw new IllegalStateException("You cannot deactivate your own account");
        }

        log.debug("Setting active status to {} for user: {}", active, userId);
        user.setActive(active);
        user.setUpdatedAt(TimestampUtils.now());
        User updatedUser = userRepository.save(user);

        // Log the activity
        userActivityService.logActivity("Set active status to " + active + " for user ID: " + userId);

        return updatedUser;
    }

    @Override
    public User setUserRole(String userId, User.Role role) {
        // Only admins can change user roles
        if (!userContext.isAdmin()) {
            throw new AccessDeniedException("Only administrators can change user roles");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Don't allow changing your own role
        User currentUser = userContext.getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(userId)) {
            throw new IllegalStateException("You cannot change your own role");
        }

        log.debug("Setting role to {} for user: {}", role, userId);
        user.setRole(role);
        user.setUpdatedAt(TimestampUtils.now());
        User updatedUser = userRepository.save(user);

        // Log the activity
        userActivityService.logActivity("Set role to " + role + " for user ID: " + userId);

        return updatedUser;
    }

    /**
     * Get the current authenticated user
     *
     * @return the current user or null if not authenticated
     */
    public User getCurrentUser() {
        return userContext.getCurrentUser();
    }
}
