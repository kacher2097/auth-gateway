package com.authenhub.service.interfaces;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;

public interface IAnalyticsService {
    ApiResponse<?> getStatisticsInternal(StatisticSearchRequest request);
    ApiResponse<?> getDashboardData(StatisticSearchRequest request);
    ApiResponse<?> getAccessStatsInternal(StatisticSearchRequest request);
    ApiResponse<?> getTrafficDataInternal(StatisticSearchRequest request);
    ApiResponse<?> getUserActivityDataInternal(StatisticSearchRequest request);
    ApiResponse<?> getTopEndpointsInternal(StatisticSearchRequest request);
    ApiResponse<?> getTopUsersInternal(StatisticSearchRequest request);
    ApiResponse<?> getLoginActivityInternal(StatisticSearchRequest request);
}
