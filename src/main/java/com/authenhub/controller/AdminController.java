package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.UserRepository;
import com.authenhub.service.AccessLogService;
import com.authenhub.service.UserManagementService;
import com.authenhub.service.UserService;
import com.authenhub.utils.TimestampUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserManagementService userManagementService;
    private final AccessLogService accessLogService;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        return getUsersList(null, null, null);
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse> getUsersPost(@RequestBody(required = false) Map<String, Object> params) {
        Integer page = params != null && params.containsKey("page") ? Integer.valueOf(params.get("page").toString()) : null;
        Integer limit = params != null && params.containsKey("limit") ? Integer.valueOf(params.get("limit").toString()) : null;
        String search = params != null && params.containsKey("search") ? params.get("search").toString() : null;

        return getUsersList(page, limit, search);
    }

    private ResponseEntity<ApiResponse> getUsersList(Integer page, Integer limit, String search) {
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
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String id) {
        return getUserByIdInternal(id);
    }

    @PostMapping("/users/{id}")
    public ResponseEntity<ApiResponse> getUserByIdPost(@PathVariable String id) {
        return getUserByIdInternal(id);
    }

    private ResponseEntity<ApiResponse> getUserByIdInternal(String id) {
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
    public ResponseEntity<ApiResponse> getDashboardData() {
        return getDashboardDataInternal();
    }

    @PostMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardDataPost() {
        return getDashboardDataInternal();
    }

    private ResponseEntity<ApiResponse> getDashboardDataInternal() {
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

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getStatisticsInternal(startDate, endDate);
    }

    @PostMapping("/statistics")
    public ResponseEntity<ApiResponse> getStatisticsPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getStatisticsInternal(startDate, endDate);
    }

    private ResponseEntity<ApiResponse> getStatisticsInternal(String startDateStr, String endDateStr) {
        try {
            // Parse dates or use defaults
            Timestamp startDate;
            Timestamp endDate;

            if (startDateStr != null) {
                startDate = Timestamp.valueOf(startDateStr.replace('T', ' ').substring(0, 19));
            } else {
                endDate = TimestampUtils.now();
                startDate = TimestampUtils.addDays(endDate, -30);
            }

            if (endDateStr != null) {
                endDate = Timestamp.valueOf(endDateStr.replace('T', ' ').substring(0, 19));
            } else {
                endDate = TimestampUtils.now();
            }

            // Get user statistics
            long totalUsers = userService.countTotalUsers();
            long activeUsers = userService.countUsersByActive(true);
            long newUsers = userService.countUsersByCreatedAtBetween(startDate, endDate);

            // Get login statistics from access logs
            Map<String, Object> accessStats = accessLogService.getAccessStats(startDate, endDate);

            // Create statistics response
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", totalUsers);
            statistics.put("activeUsers", activeUsers);
            statistics.put("newUsers", newUsers);
            statistics.put("loginAttempts", accessStats.getOrDefault("totalLogins", 0));
            statistics.put("successfulLogins", accessStats.getOrDefault("successfulLogins", 0));
            statistics.put("failedLogins", accessStats.getOrDefault("failedLogins", 0));

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Statistics retrieved successfully")
                    .data(statistics)
                    .build());
        } catch (Exception e) {
            log.error("Error getting statistics", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/login-activity")
    public ResponseEntity<ApiResponse> getLoginActivity(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getLoginActivityInternal(startDate, endDate);
    }

    @PostMapping("/login-activity")
    public ResponseEntity<ApiResponse> getLoginActivityPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getLoginActivityInternal(startDate, endDate);
    }

    private ResponseEntity<ApiResponse> getLoginActivityInternal(String startDateStr, String endDateStr) {
        // Parse dates or use defaults
        Timestamp startDate;
        Timestamp endDate;

        if (startDateStr != null) {
            startDate = Timestamp.valueOf(startDateStr.replace('T', ' ').substring(0, 19));
        } else {
            endDate = TimestampUtils.now();
            startDate = TimestampUtils.addDays(endDate, -30);
        }

        if (endDateStr != null) {
            endDate = Timestamp.valueOf(endDateStr.replace('T', ' ').substring(0, 19));
        } else {
            endDate = TimestampUtils.now();
        }

        // Get login activity from access logs
        List<Map<String, Object>> loginActivity = accessLogService.getLoginActivity(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Login activity retrieved successfully")
                .data(loginActivity)
                .build());
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
