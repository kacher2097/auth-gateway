package com.authenhub.service;

import com.authenhub.dto.UserUpdateRequest;
import com.authenhub.entity.User;
import com.authenhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    public User updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
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
        
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }
    
    public User setUserActiveStatus(String userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setActive(active);
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }
    
    public User setUserRole(String userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(role);
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }
}
