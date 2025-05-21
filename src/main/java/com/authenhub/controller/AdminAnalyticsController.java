package com.authenhub.controller;

import com.authenhub.bean.AccessStatsRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;
import com.authenhub.service.interfaces.IAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics", description = "API để quản lý và xem thống kê dữ liệu hệ thống")
public class AdminAnalyticsController {

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
        return ApiResponse.success(analyticsService.getAccessStatsInternal(request));
    }

    @PostMapping("/traffic")
    @Operation(summary = "Lấy dữ liệu lưu lượng truy cập (POST)",
            description = "Lấy dữ liệu về lưu lượng truy cập vào hệ thống sử dụng phương thức POST.")
    public ApiResponse<?> getTrafficDataPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getTrafficDataInternal(request);
    }

    @PostMapping("/login-activity")
    @Operation(summary = "Lấy dữ liệu hoạt động đăng nhập",
            description = "Lấy dữ liệu về các hoạt động đăng nhập vào hệ thống.")
    public ApiResponse<?> getLoginActivityPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getLoginActivityInternal(request);
    }

    @PostMapping("/user-activity")
    @Operation(summary = "Lấy dữ liệu hoạt động người dùng (POST)",
            description = "Lấy dữ liệu về các hoạt động của người dùng trong hệ thống sử dụng phương thức POST.")
    public ApiResponse<?> getUserActivityDataPost(@RequestBody StatisticSearchRequest request) {
        return analyticsService.getUserActivityDataInternal(request);
    }
}
