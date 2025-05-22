package com.authenhub.service.interfaces;

import com.authenhub.bean.AccessStatsRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;

public interface IAnalyticsService {
    ApiResponse<?> getStatisticsInternal(StatisticSearchRequest request);
    ApiResponse<?> getDashboardData(StatisticSearchRequest request);
    StatisticGetResponse getAccessStatsInternal(AccessStatsRequest request);
    ApiResponse<?> getTrafficDataInternal(StatisticSearchRequest request);
    ApiResponse<?> getUserActivityDataInternal(StatisticSearchRequest request);
    ApiResponse<?> getLoginActivityInternal(StatisticSearchRequest request);
}
