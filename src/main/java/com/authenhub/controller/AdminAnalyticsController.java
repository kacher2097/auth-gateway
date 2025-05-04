package com.authenhub.controller;

import com.authenhub.bean.AccessStatsRequest;
import com.authenhub.bean.DateRangeRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;
import com.authenhub.service.AccessLogService;
import com.authenhub.service.interfaces.IAnalyticsService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AccessLogService accessLogService;
    private final IAnalyticsService analyticsService;

    @PostMapping("/dashboard")
    public ApiResponse<?> getDashboardDataPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getDashboardData(request);
    }

    @PostMapping("/statistics")
    public ApiResponse<?> getStatisticsPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getStatisticsInternal(request);
    }

    @PostMapping("/access-stats")
    public ApiResponse<?> getAccessStats(@RequestBody AccessStatsRequest request) {
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        if (startDate == null) {
            endDate = TimestampUtils.now();
            startDate = TimestampUtils.addDays(endDate, -30);
        }

        if (endDate == null) {
            endDate = TimestampUtils.now();
        }
        StatisticSearchRequest searchRequest = StatisticSearchRequest.builder()
                .endDate(endDate)
                .startDate(startDate)
                .build();
        StatisticGetResponse stats = accessLogService.getAccessStats(searchRequest);

        return ApiResponse.success(stats);
    }

    @GetMapping("/traffic")
    public ApiResponse<?> getTrafficData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getTrafficDataInternal(startDate, endDate);
    }

    @PostMapping("/traffic")
    public ApiResponse<?> getTrafficDataPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getTrafficDataInternal(startDate, endDate);
    }

    private ApiResponse<?> getTrafficDataInternal(String startDateStr, String endDateStr) {
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
        StatisticSearchRequest searchRequest = StatisticSearchRequest.builder()
                .endDate(endDate)
                .startDate(startDate)
                .build();
        StatisticGetResponse accessStats = accessLogService.getAccessStats(searchRequest);

        // Extract daily visits as traffic data
        Long dailyVisits = accessStats.getDailyVisits();

        return ApiResponse.success(dailyVisits);
    }

    @GetMapping("/user-activity")
    public ApiResponse<?> getUserActivityData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getUserActivityDataInternal(startDate, endDate);
    }

    @PostMapping("/user-activity")
    public ApiResponse<?> getUserActivityDataPost(@RequestBody(required = false) Map<String, Object> params) {
        String startDate = params != null && params.containsKey("startDate") ? params.get("startDate").toString() : null;
        String endDate = params != null && params.containsKey("endDate") ? params.get("endDate").toString() : null;

        return getUserActivityDataInternal(startDate, endDate);
    }

    private ApiResponse<?> getUserActivityDataInternal(String startDateStr, String endDateStr) {
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
        StatisticSearchRequest searchRequest = StatisticSearchRequest.builder()
                .endDate(endDate)
                .startDate(startDate)
                .build();
        StatisticGetResponse stats = accessLogService.getAccessStats(searchRequest);

        // For this example, we'll just return the daily visits data reformatted as user activity
        Long dailyVisits = stats.getDailyVisits();

        return ApiResponse.success(dailyVisits);
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
