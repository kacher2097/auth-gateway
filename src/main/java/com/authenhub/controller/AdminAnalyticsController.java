package com.authenhub.controller;

import com.authenhub.bean.AccessStatsRequest;
import com.authenhub.dto.ApiResponse;
import com.authenhub.service.AccessLogService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AccessLogService accessLogService;

    @PostMapping("/access-stats")
    public ResponseEntity<ApiResponse> getAccessStats(@RequestBody AccessStatsRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        if (startDate == null) {
            endDate = TimestampUtils.now();
            startDate = TimestampUtils.addDays(endDate, -30);
        }

        if (endDate == null) {
            endDate = TimestampUtils.now();
        }
        Map<String, Object> stats = accessLogService.getAccessStats(startDate, endDate);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Access statistics retrieved successfully")
                .data(stats)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/traffic")
    public ResponseEntity<ApiResponse> getTrafficData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getTrafficDataInternal(startDate, endDate);
    }

    @PostMapping("/traffic")
    public ResponseEntity<ApiResponse> getTrafficDataPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getTrafficDataInternal(startDate, endDate);
    }

    private ResponseEntity<ApiResponse> getTrafficDataInternal(String startDateStr, String endDateStr) {
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

        // Get traffic data from access logs
        Map<String, Object> accessStats = accessLogService.getAccessStats(startDate, endDate);

        // Extract daily visits as traffic data
        Object dailyVisits = accessStats.get("dailyVisits");

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Traffic data retrieved successfully")
                .data(dailyVisits)
                .build());
    }

    @GetMapping("/user-activity")
    public ResponseEntity<ApiResponse> getUserActivityData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getUserActivityDataInternal(startDate, endDate);
    }

    @PostMapping("/user-activity")
    public ResponseEntity<ApiResponse> getUserActivityDataPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getUserActivityDataInternal(startDate, endDate);
    }

    private ResponseEntity<ApiResponse> getUserActivityDataInternal(String startDateStr, String endDateStr) {
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

        // Get user activity data
        // This is a simplified implementation - in a real app, you would have more detailed user activity data
        Map<String, Object> accessStats = accessLogService.getAccessStats(startDate, endDate);

        // For this example, we'll just return the daily visits data reformatted as user activity
        Object dailyVisits = accessStats.get("dailyVisits");

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User activity data retrieved successfully")
                .data(dailyVisits)
                .build());
    }
}
