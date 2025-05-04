package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;
import com.authenhub.bean.user.UserSearchResponse;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.AccessLogService;
import com.authenhub.service.UserManagementService;
import com.authenhub.service.UserService;
import com.authenhub.utils.TimestampUtils;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JwtService jwtService;
    private final UserService userService;
    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;
    private final AccessLogService accessLogService;
    private final UserManagementService userManagementService;

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
                    .roleId(user.getRoleId())
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
    public ApiResponse<?> getUserById(@PathVariable Long id) {
        return getUserByIdInternal(id);
    }

    @PostMapping("/users/{id}")
    public ApiResponse<?> getUserByIdPost(@PathVariable Long id) {
        return getUserByIdInternal(id);
    }

    private ApiResponse<?> getUserByIdInternal(Long id) {
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
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        User updatedUser = userManagementService.updateUser(id, request);
        updatedUser.setPassword(null); // Remove sensitive information

        return ApiResponse.success(updatedUser);
    }

    @PutMapping("/users/{id}/activate")
    public ApiResponse<?> activateUser(@PathVariable Long id) {
        User user = userManagementService.setUserActiveStatus(id, true);
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/deactivate")
    public ApiResponse<?> deactivateUser(@PathVariable Long id) {
        User user = userManagementService.setUserActiveStatus(id, false);
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/promote-to-admin")
    public ApiResponse<?> promoteToAdmin(@PathVariable Long id) {
        User user = userManagementService.setUserRole(id, "ADMIN");
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/demote-to-user")
    public ApiResponse<?> demoteToUser(@PathVariable Long id) {
        User user = userManagementService.setUserRole(id, "USER");
        return ApiResponse.success(user);
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
}
