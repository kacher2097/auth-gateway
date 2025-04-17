package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.dto.request.DateRangeRequest;
import com.authenhub.service.interfaces.IAccessLogService;
import com.authenhub.service.interfaces.IUserService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ApiResponse> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        // User stats
        data.put("totalUsers", userService.countTotalUsers());
        data.put("activeUsers", userService.countUsersByActive(true));
        data.put("inactiveUsers", userService.countUsersByActive(false));
        data.put("adminUsers", userService.countUsersByRole(com.authenhub.entity.User.Role.ADMIN));
        data.put("regularUsers", userService.countUsersByRole(com.authenhub.entity.User.Role.USER));
        data.put("socialLoginUsers", userService.countUsersBySocialLogin());
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Dashboard data retrieved successfully")
                .data(data)
                .build());
    }
    
    /**
     * Get access stats
     *
     * @param request date range request
     * @return access stats
     */
    @PostMapping("/access-stats")
    public ResponseEntity<ApiResponse> getAccessStats(@RequestBody DateRangeRequest request) {
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
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Access stats retrieved successfully")
                .data(accessStats)
                .build());
    }
    
    /**
     * Get traffic data
     *
     * @param request date range request
     * @return traffic data
     */
    @PostMapping("/traffic")
    public ResponseEntity<ApiResponse> getTrafficData(@RequestBody DateRangeRequest request) {
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
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Traffic data retrieved successfully")
                .data(dailyVisits)
                .build());
    }
    
    /**
     * Get login activity
     *
     * @param request date range request
     * @return login activity
     */
    @PostMapping("/login-activity")
    public ResponseEntity<ApiResponse> getLoginActivity(@RequestBody DateRangeRequest request) {
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
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Login activity retrieved successfully")
                .data(accessLogService.getLoginActivity(startDate, endDate))
                .build());
    }
    
    /**
     * Get top endpoints
     *
     * @param request date range request
     * @return top endpoints
     */
    @PostMapping("/top-endpoints")
    public ResponseEntity<ApiResponse> getTopEndpoints(@RequestBody DateRangeRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();
        
        // Default to last 30 days if no dates provided
        if (startDate == null) {
            startDate = TimestampUtils.addDays(TimestampUtils.now(), -30);
        }
        
        if (endDate == null) {
            endDate = TimestampUtils.now();
        }
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Top endpoints retrieved successfully")
                .data(accessLogService.getTopEndpoints(startDate, endDate))
                .build());
    }
    
    /**
     * Get top users
     *
     * @param request date range request
     * @return top users
     */
    @PostMapping("/top-users")
    public ResponseEntity<ApiResponse> getTopUsers(@RequestBody DateRangeRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();
        
        // Default to last 30 days if no dates provided
        if (startDate == null) {
            startDate = TimestampUtils.addDays(TimestampUtils.now(), -30);
        }
        
        if (endDate == null) {
            endDate = TimestampUtils.now();
        }
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Top users retrieved successfully")
                .data(accessLogService.getTopUsers(startDate, endDate))
                .build());
    }
}
