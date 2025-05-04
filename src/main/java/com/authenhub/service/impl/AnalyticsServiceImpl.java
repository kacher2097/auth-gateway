package com.authenhub.service.impl;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.dashboard.DashboardData;
import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;
import com.authenhub.controller.AdminController;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.AccessLogService;
import com.authenhub.service.UserService;
import com.authenhub.service.interfaces.IAnalyticsService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements IAnalyticsService {

    private final UserService userService;
    private final AccessLogService accessLogService;
    private final UserJpaRepository userRepository;

    @Override
    public ApiResponse<?> getStatisticsInternal(StatisticSearchRequest request) {
        // Parse dates or use defaults
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();
        try {
            // Get user statistics
            long totalUsers = userService.countTotalUsers();
            long activeUsers = userService.countUsersByActive(true);
            long newUsers = userService.countUsersByCreatedAtBetween(startDate, endDate);

            // Get login statistics from access logs
            StatisticSearchRequest searchRequest = StatisticSearchRequest.builder()
                    .endDate(endDate)
                    .startDate(startDate)
                    .build();
            StatisticGetResponse accessStats = accessLogService.getAccessStats(searchRequest);

            // Create statistics response
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalUsers", totalUsers);
            statistics.put("activeUsers", activeUsers);
            statistics.put("newUsers", newUsers);

            // Get login statistics from the StatisticGetResponse object
            statistics.put("loginAttempts", accessStats.getTotalLogins());
            statistics.put("successfulLogins", accessStats.getSuccessfulLogins());
            statistics.put("failedLogins", accessStats.getFailedLogins());

            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("Error getting statistics", e);
            return ApiResponse.error("400", "Error getting statistics: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> getDashboardData(StatisticSearchRequest request) {
        return getDashboardDataInternal();
    }

    @Override
    public ApiResponse<?> getAccessStatsInternal(StatisticSearchRequest request) {
        return null;
    }

    @Override
    public ApiResponse<?> getTrafficDataInternal(StatisticSearchRequest request) {
        return null;
    }

    @Override
    public ApiResponse<?> getUserActivityDataInternal(StatisticSearchRequest request) {
        return null;
    }

    @Override
    public ApiResponse<?> getTopEndpointsInternal(StatisticSearchRequest request) {
        return null;
    }

    @Override
    public ApiResponse<?> getTopUsersInternal(StatisticSearchRequest request) {
        return null;
    }

    @Override
    public ApiResponse<?> getLoginActivityInternal(StatisticSearchRequest request) {
        return null;
    }

    private ApiResponse<?> getDashboardDataInternal() {
        long userCount = userRepository.count();
        long adminCount = userRepository.countByRole("ADMIN");
        long regularUserCount = userRepository.countByRole("USER");
        DashboardData dashboardData = DashboardData.builder()
                .totalUsers(userCount)
                .adminUsers(adminCount)
                .regularUsers(regularUserCount)
                .build();
        return ApiResponse.success(dashboardData);
    }
}
