package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.UserRepository;
import com.authenhub.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserManagementService userManagementService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        // Remove sensitive information
        users.forEach(user -> user.setPassword(null));

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setPassword(null); // Remove sensitive information

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User retrieved successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        User updatedUser = userManagementService.updateUser(id, request);
        updatedUser.setPassword(null); // Remove sensitive information

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable String id) {
        User user = userManagementService.setUserActiveStatus(id, true);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User activated successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable String id) {
        User user = userManagementService.setUserActiveStatus(id, false);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User deactivated successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/promote-to-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> promoteToAdmin(@PathVariable String id) {
        User user = userManagementService.setUserRole(id, User.Role.ADMIN);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User promoted to admin successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/demote-to-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> demoteToUser(@PathVariable String id) {
        User user = userManagementService.setUserRole(id, User.Role.USER);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User demoted to regular user successfully")
                .data(user)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getDashboardData() {
        long userCount = userRepository.count();
        long adminCount = userRepository.countByRole(User.Role.ADMIN);
        long regularUserCount = userRepository.countByRole(User.Role.USER);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Dashboard data retrieved successfully")
                .data(new DashboardData(userCount, adminCount, regularUserCount))
                .build();

        return ResponseEntity.ok(response);
    }

    private static class DashboardData {
        private final long totalUsers;
        private final long adminUsers;
        private final long regularUsers;

        public DashboardData(long totalUsers, long adminUsers, long regularUsers) {
            this.totalUsers = totalUsers;
            this.adminUsers = adminUsers;
            this.regularUsers = regularUsers;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getAdminUsers() {
            return adminUsers;
        }

        public long getRegularUsers() {
            return regularUsers;
        }
    }
}
