package com.authenhub.controller;

import com.authenhub.bean.DateRangeRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.entity.mongo.User;
import com.authenhub.service.interfaces.IAccessLogService;
import com.authenhub.service.interfaces.IUserService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for analytics operations
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final IAccessLogService accessLogService;
    private final IUserService userService;

    /**
     * Get dashboard data
     *
     * @return dashboard data
     */
    @GetMapping("/dashboard")
    public ApiResponse<?> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // User stats
        data.put("totalUsers", userService.countTotalUsers());
        data.put("activeUsers", userService.countUsersByActive(true));
        data.put("inactiveUsers", userService.countUsersByActive(false));
        data.put("adminUsers", userService.countUsersByRole(User.Role.ADMIN));
        data.put("regularUsers", userService.countUsersByRole(User.Role.USER));
        data.put("socialLoginUsers", userService.countUsersBySocialLogin());

        return ApiResponse.success(data);
    }

    /**
     * Get access stats
     *
     * @param request date range request
     * @return access stats
     */
    @PostMapping("/access-stats")
    public ApiResponse<?> getAccessStats(@RequestBody DateRangeRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        // Default to last 30 days if no dates provided
        if (startDate == null) {
            startDate = TimestampUtils.addDays(TimestampUtils.now(), -30);
        }

        if (endDate == null) {
            endDate = TimestampUtils.now();
        }

        Map<String, Object> accessStats = accessLogService.getAccessStats(startDate, endDate);

        return ApiResponse.success(accessStats);
    }

    /**
     * Get traffic data
     *
     * @param request date range request
     * @return traffic data
     */
    @PostMapping("/traffic")
    public ApiResponse<?> getTrafficData(@RequestBody DateRangeRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        // Default to last 30 days if no dates provided
        if (startDate == null) {
            startDate = TimestampUtils.addDays(TimestampUtils.now(), -30);
        }

        if (endDate == null) {
            endDate = TimestampUtils.now();
        }

        // Get daily visits as traffic data
        Map<String, Object> accessStats = accessLogService.getAccessStats(startDate, endDate);
        Object dailyVisits = accessStats.get("dailyVisits");

        return ApiResponse.success(dailyVisits);
    }

    /**
     * Get login activity
     *
     * @param request date range request
     * @return login activity
     */
    @PostMapping("/login-activity")
    public ApiResponse<?> getLoginActivity(@RequestBody DateRangeRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        // Default to last 30 days if no dates provided
        if (startDate == null) {
            startDate = TimestampUtils.addDays(TimestampUtils.now(), -30);
        }

        if (endDate == null) {
            endDate = TimestampUtils.now();
        }

        // Get login activity
        return ApiResponse.success(accessLogService.getLoginActivity(startDate, endDate));
    }

    /**
     * Get top endpoints
     *
     * @param request date range request
     * @return top endpoints
     */
    @PostMapping("/top-endpoints")
    public ApiResponse<?> getTopEndpoints(@RequestBody DateRangeRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        // Default to last 30 days if no dates provided
        if (startDate == null) {
            startDate = TimestampUtils.addDays(TimestampUtils.now(), -30);
        }

        if (endDate == null) {
            endDate = TimestampUtils.now();
        }

        return ApiResponse.success(accessLogService.getTopEndpoints(startDate, endDate));
    }

    /**
     * Get top users
     *
     * @param request date range request
     * @return top users
     */
    @PostMapping("/top-users")
    public ApiResponse<?> getTopUsers(@RequestBody DateRangeRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        // Default to last 30 days if no dates provided
        if (startDate == null) {
            startDate = TimestampUtils.addDays(TimestampUtils.now(), -30);
        }

        if (endDate == null) {
            endDate = TimestampUtils.now();
        }

        return ApiResponse.success(accessLogService.getTopUsers(startDate, endDate));
    }
}
