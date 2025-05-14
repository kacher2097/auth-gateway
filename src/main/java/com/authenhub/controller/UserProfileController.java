package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.constant.enums.ApiResponseCode;
import com.authenhub.entity.User;
import com.authenhub.service.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user profile operations
 */
@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserContext userContext;

    /**
     * Get the current user's profile
     *
     * @return the current user's profile
     */
    @GetMapping
    public ApiResponse<?> getCurrentUserProfile() {
        User currentUser = userContext.getCurrentUser();

        if (currentUser == null) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, "User not authenticated");
        }

        // You can create a DTO to hide sensitive information if needed
        return ApiResponse.success(currentUser);
    }

    /**
     * Check if the current user is an admin
     *
     * @return true if admin, false otherwise
     */
    @GetMapping("/is-admin")
    public ApiResponse<?> isAdmin() {
        if (!userContext.isAuthenticated()) {
            return ApiResponse.error(ApiResponseCode.FORBIDDEN, "User not authenticated");
        }

        boolean isAdmin = userContext.isAdmin();
        return ApiResponse.success(isAdmin);
    }
}
