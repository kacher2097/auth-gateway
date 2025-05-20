package com.authenhub.controller;

import com.authenhub.bean.AccessStatsRequest;
import com.authenhub.bean.DateRangeRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;
import com.authenhub.service.AccessLogService;
import com.authenhub.service.interfaces.IAnalyticsService;
import com.authenhub.utils.TimestampUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Analytics", description = "API để quản lý và xem thống kê dữ liệu hệ thống")
public class AdminAnalyticsController {

    private final AccessLogService accessLogService;
    private final IAnalyticsService analyticsService;

    @PostMapping("/dashboard")
    @Operation(summary = "Lấy dữ liệu bảng điều khiển",
            description = "Lấy dữ liệu tổng quan cho bảng điều khiển của admin.")
    public ApiResponse<?> getDashboardDataPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getDashboardData(request);
    }

    @PostMapping("/statistics")
    @Operation(summary = "Lấy dữ liệu thống kê",
            description = "Lấy dữ liệu thống kê chi tiết của hệ thống.")
    public ApiResponse<?> getStatisticsPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getStatisticsInternal(request);
    }

    @PostMapping("/access-stats")
    @Operation(summary = "Lấy thống kê truy cập",
            description = "Lấy thống kê về các lượt truy cập vào hệ thống.")
    public ApiResponse<?> getAccessStats(@RequestBody AccessStatsRequest request) {
        return analyticsService.getAccessStatsInternal(request);
    }

    @GetMapping("/traffic")
    @Operation(summary = "Lấy dữ liệu lưu lượng truy cập",
            description = "Lấy dữ liệu về lưu lượng truy cập vào hệ thống.")
    public ApiResponse<?> getTrafficData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getTrafficDataInternal(startDate, endDate);
    }

    @PostMapping("/traffic")
    @Operation(summary = "Lấy dữ liệu lưu lượng truy cập (POST)",
            description = "Lấy dữ liệu về lưu lượng truy cập vào hệ thống sử dụng phương thức POST.")
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

    @PostMapping("/login-activity")
    @Operation(summary = "Lấy dữ liệu hoạt động đăng nhập",
            description = "Lấy dữ liệu về các hoạt động đăng nhập vào hệ thống.")
    public ApiResponse<?> getLoginActivityPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getLoginActivityInternal(request);
    }

    @GetMapping("/user-activity")
    @Operation(summary = "Lấy dữ liệu hoạt động người dùng",
            description = "Lấy dữ liệu về các hoạt động của người dùng trong hệ thống.")
    public ApiResponse<?> getUserActivityData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return getUserActivityDataInternal(startDate, endDate);
    }

    @PostMapping("/user-activity")
    @Operation(summary = "Lấy dữ liệu hoạt động người dùng (POST)",
            description = "Lấy dữ liệu về các hoạt động của người dùng trong hệ thống sử dụng phương thức POST.")
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
    @Operation(summary = "Lấy danh sách endpoint phổ biến nhất",
            description = "Lấy danh sách các endpoint được truy cập nhiều nhất trong hệ thống.")
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
    @Operation(summary = "Lấy danh sách người dùng hoạt động nhiều nhất",
            description = "Lấy danh sách các người dùng có nhiều hoạt động nhất trong hệ thống.")
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
