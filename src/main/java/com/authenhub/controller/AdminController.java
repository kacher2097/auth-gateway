package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.user.UserSearchResponse;
import com.authenhub.entity.mongo.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.RoleRepository;
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
import java.util.*;

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
    private final RoleRepository roleRepository;

    @GetMapping("/users")
    public com.authenhub.bean.common.ApiResponse<?> getAllUsers() {
        return getUsersList(null, null, null);
    }

    @PostMapping("/users")
    public com.authenhub.bean.common.ApiResponse<?> getUsersPost(@RequestBody(required = false) Map<String, Object> params) {
        Integer page = params != null && params.containsKey("page") ? Integer.valueOf(params.get("page").toString()) : null;
        Integer limit = params != null && params.containsKey("limit") ? Integer.valueOf(params.get("limit").toString()) : null;
        String search = params != null && params.containsKey("search") ? params.get("search").toString() : null;

        return getUsersList(page, limit, search);
    }

    private com.authenhub.bean.common.ApiResponse<?> getUsersList(Integer page, Integer limit, String search) {
        List<User> users = userRepository.findAll();
        List<UserSearchResponse> response = new ArrayList<>();
        for (User user : users) {
            UserSearchResponse userSearchResponse = UserSearchResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .avatar(user.getAvatar())
                    .role(user.getRoleId())
                    .active(user.isActive())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .lastLogin(user.getLastLogin())
                    .build();
            roleRepository.findById(user.getRoleId()).ifPresent(role -> userSearchResponse.setRole(role.getDisplayName()));
            response.add(userSearchResponse);
        }
        log.info("Get all users successfully total {} users", response.size());
        return com.authenhub.bean.common.ApiResponse.success(response);
    }

    @GetMapping("/users/{id}")
    public ApiResponse<?> getUserById(@PathVariable String id) {
        return getUserByIdInternal(id);
    }

    @PostMapping("/users/{id}")
    public ApiResponse<?> getUserByIdPost(@PathVariable String id) {
        return getUserByIdInternal(id);
    }

    private ApiResponse<?> getUserByIdInternal(String id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ApiResponse.error("404", "User not found");
        }

        User user = userOpt.get();
        user.setPassword(null); // Remove sensitive information

        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}")
    public ApiResponse<?> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        User updatedUser = userManagementService.updateUser(id, request);
        updatedUser.setPassword(null); // Remove sensitive information

        return ApiResponse.success(updatedUser);
    }

    @PutMapping("/users/{id}/activate")
    public ApiResponse<?> activateUser(@PathVariable String id) {
        User user = userManagementService.setUserActiveStatus(id, true);
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/deactivate")
    public ApiResponse<?> deactivateUser(@PathVariable String id) {
        User user = userManagementService.setUserActiveStatus(id, false);
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/promote-to-admin")
    public ApiResponse<?> promoteToAdmin(@PathVariable String id) {
        User user = userManagementService.setUserRole(id, User.Role.ADMIN);
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/demote-to-user")
    public ApiResponse<?> demoteToUser(@PathVariable String id) {
        User user = userManagementService.setUserRole(id, User.Role.USER);
        return ApiResponse.success(user);
    }

    @GetMapping("/dashboard")
    public ApiResponse<?> getDashboardData() {
        return getDashboardDataInternal();
    }

    @PostMapping("/dashboard")
    public ApiResponse<?> getDashboardDataPost() {
        return getDashboardDataInternal();
    }

    private ApiResponse<?> getDashboardDataInternal() {
        long userCount = userRepository.count();
        long adminCount = userRepository.countByRole(User.Role.ADMIN);
        long regularUserCount = userRepository.countByRole(User.Role.USER);

        return ApiResponse.success(new DashboardData(userCount, adminCount, regularUserCount));
    }

    @GetMapping("/statistics")
    public ApiResponse<?> getStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getStatisticsInternal(startDate, endDate);
    }

    @PostMapping("/statistics")
    public ApiResponse<?> getStatisticsPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getStatisticsInternal(startDate, endDate);
    }

    private ApiResponse<?> getStatisticsInternal(String startDateStr, String endDateStr) {
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

            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("Error getting statistics", e);
            return ApiResponse.error("400", "Error getting statistics: " + e.getMessage());
        }
    }

    @GetMapping("/login-activity")
    public ApiResponse<?> getLoginActivity(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getLoginActivityInternal(startDate, endDate);
    }

    @PostMapping("/login-activity")
    public ApiResponse<?> getLoginActivityPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getLoginActivityInternal(startDate, endDate);
    }

    private ApiResponse<?> getLoginActivityInternal(String startDateStr, String endDateStr) {
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

        return ApiResponse.success(loginActivity);
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
