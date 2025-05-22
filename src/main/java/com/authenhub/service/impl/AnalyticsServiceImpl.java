package com.authenhub.service.impl;

import com.authenhub.bean.AccessStatsRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.dashboard.DashboardData;
import com.authenhub.bean.statistic.LoginActivityBean;
import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticItem;
import com.authenhub.bean.statistic.StatisticSearchRequest;
import com.authenhub.entity.mongo.AccessLog;
import com.authenhub.repository.AccessLogRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.UserService;
import com.authenhub.service.interfaces.IAnalyticsService;
import com.authenhub.utils.MongoQueryUtils;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements IAnalyticsService {

    private final UserService userService;
    private final MongoTemplate mongoTemplate;
    private final UserJpaRepository userRepository;
    private final AccessLogRepository accessLogRepository;

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

            Criteria dateRangeCriteria = Criteria.where("timestamp").gte(startDate).lte(endDate);
            Criteria endpointCriteria = Criteria.where("endpoint").is("/auth/login");
            Query totalLoginsQuery = new Query(new Criteria().andOperator(dateRangeCriteria, endpointCriteria));
            long totalLogins = mongoTemplate.count(totalLoginsQuery, "access_logs");

            // Build query for successful logins
            Criteria statusCodeCriteria = Criteria.where("statusCode").is(200);
            Query successfulLoginsQuery = new Query(new Criteria().andOperator(dateRangeCriteria, endpointCriteria, statusCodeCriteria));
            long successfulLogins = mongoTemplate.count(successfulLoginsQuery, "access_logs");

            long failedLogins = totalLogins - successfulLogins;

            StatisticGetResponse accessStats = StatisticGetResponse.builder()
                    .totalUsers(totalUsers)
                    .activeUsers(activeUsers)
                    .newUsers(newUsers)
                    .loginAttempts(totalLogins)
                    .successfulLogins(successfulLogins)
                    .failedLogins(failedLogins)
                    .build();

            return ApiResponse.success(accessStats);
        } catch (Exception e) {
            log.error("Error getting statistics", e);
            return ApiResponse.error("400", "Error getting statistics: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<?> getDashboardData(StatisticSearchRequest request) {
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

    @Override
    public StatisticGetResponse getAccessStatsInternal(AccessStatsRequest request) {

        Timestamp start = request.getStartDate();
        Timestamp end = request.getEndDate();
        MatchOperation matchOperation = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        GroupOperation groupByBrowser = Aggregation.group("browser")
                .count().as("count");

        GroupOperation sumCounts = Aggregation.group()
                .sum("count").as("count");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupByBrowser,
                sumCounts
        );

        AggregationResults<StatisticItem> results = mongoTemplate.aggregate(
                aggregation, "access_logs", StatisticItem.class);

        // Lấy kết quả
        StatisticItem result = results.getUniqueMappedResult();
        long totalCount = result != null ? result.getCount() : 0;

        StatisticGetResponse response = StatisticGetResponse.builder()
                .totalVisits(totalCount)
                .totalLogins(countLoginsByStatus(start, end, 200))
                .browserStats(getBrowserStats(start, end))
                .deviceStats(getDeviceTypeStats(start, end))
                .topEndpoints(getTopEndpoints(start, end))
                .topUsers(getTopUsers(start, end))
                .build();
        log.info("Get response success: ");
        return response;
    }

    @Override
    public ApiResponse<?> getTrafficDataInternal(StatisticSearchRequest request) {
        // Parse dates or use defaults
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        // Get traffic data from access logs
        AccessStatsRequest searchRequest = AccessStatsRequest.builder()
                .endDate(endDate)
                .startDate(startDate)
                .build();
        StatisticGetResponse accessStats = getAccessStatsInternal(searchRequest);

        // Extract daily visits as traffic data
        Long dailyVisits = accessStats.getDailyVisits();
        return ApiResponse.success(dailyVisits);
    }

    @Override
    public ApiResponse<?> getUserActivityDataInternal(StatisticSearchRequest request) {
        AccessStatsRequest searchRequest = AccessStatsRequest.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        StatisticGetResponse stats = getAccessStatsInternal(searchRequest);
        Long dailyVisits = stats.getDailyVisits();
        return ApiResponse.success(dailyVisits);
    }

    @Override
    public ApiResponse<?> getLoginActivityInternal(StatisticSearchRequest request) {
        // Parse dates or use defaults
        Timestamp startDate = request.getStartDate();
        Timestamp endDate = request.getEndDate();

        // Get login activity logs
        List<AccessLog> loginLogs = accessLogRepository.findByTimestampBetween(startDate, endDate);

        // Convert to response format
        List<LoginActivityBean> loginActivityBeans = new ArrayList<>();

        for (AccessLog log : loginLogs) {
            LoginActivityBean loginActivityBean = LoginActivityBean.builder()
                    .username(log.getUsername())
                    .ip(log.getIpAddress())
                    .status(log.getStatusCode() == 200 ? "success" : "failed")
                    .timestamp(log.getTimestamp().toString())
                    .userAgent(log.getUserAgent())
                    .reason(log.getStatusCode() != 200 ? "Authentication failed" : null)
                    .build();

            loginActivityBeans.add(loginActivityBean);
        }

        return ApiResponse.success(loginActivityBeans);
    }

    private Criteria buildConditionLoginByStatus(Timestamp start, Timestamp end, Integer statusCode) {
        Criteria dateRangeCriteria = MongoQueryUtils.dateRange("timestamp", start, end);
        Criteria endpointCriteria = MongoQueryUtils.hasValue("endpoint", "/auth/login");
        Criteria statusCodeCriteria = MongoQueryUtils.hasValue("statusCode", statusCode);
        return MongoQueryUtils.and(dateRangeCriteria, endpointCriteria, statusCodeCriteria);
    }

    private long countLoginsByStatus(Timestamp start, Timestamp end, Integer statusCode) {
        Criteria criteria = buildConditionLoginByStatus(start, end, statusCode);
        MatchOperation matchOperation = Aggregation.match(criteria);
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, Aggregation.count().as("count"));
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "access_logs", Document.class);
        Document result = results.getUniqueMappedResult();
        return result != null ? ((Number) result.get("count")).longValue() : 0;
    }

    private MatchOperation buildMatchOperationTime(Timestamp start, Timestamp end) {
        Criteria dateRangeCriteria = MongoQueryUtils.dateRange("timestamp", start, end);
        return Aggregation.match(dateRangeCriteria);
    }

    private List<StatisticItem> getBrowserStats(Timestamp start, Timestamp end) {
        MatchOperation matchTime = buildMatchOperationTime(start, end);
        GroupOperation groupByBrowser = Aggregation.group("browser")
                .count().as("count");

        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "count");
        Aggregation aggregation = Aggregation.newAggregation(
                matchTime,
                groupByBrowser,
                sortByCount
        );

        AggregationResults<StatisticItem> results = mongoTemplate.aggregate(aggregation, "access_logs", StatisticItem.class);

        // Đảm bảo rằng mỗi item có name được đặt đúng
        return results.getMappedResults().stream()
                .peek(item -> {
                    // Đặt tên hiển thị bằng ID nếu name là null
                    if (item.getName() == null) {
                        item.setName(item.getId());
                    }
                })
                .collect(Collectors.toList());
    }

    private List<StatisticItem> getDeviceTypeStats(Timestamp start, Timestamp end) {
        MatchOperation matchTime = buildMatchOperationTime(start, end);
        // Nhóm theo loại thiết bị
        GroupOperation groupByDeviceType = Aggregation.group("deviceType")
                .count().as("count");

        // Sắp xếp theo số lượng giảm dần
        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "count");

        // Tạo aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchTime,
                groupByDeviceType,
                sortByCount
        );

        // Thực thi aggregation
        AggregationResults<StatisticItem> results = mongoTemplate.aggregate(aggregation, "access_logs", StatisticItem.class);
        return results.getMappedResults().stream()
                .peek(item -> {
                    // Đặt tên hiển thị bằng ID nếu name là null
                    if (item.getName() == null) {
                        item.setName(item.getId());
                    }
                }).toList();
    }

    private List<StatisticItem> getTopEndpoints(Timestamp start, Timestamp end) {
        MatchOperation matchTime = buildMatchOperationTime(start, end);
        // Nhóm theo endpoint và đếm, tính thời gian phản hồi trung bình
        GroupOperation groupByEndpoint = Aggregation.group("endpoint")
                .count().as("count")
                .avg("responseTimeMs").as("avgResponseTime");

        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "count");

        LimitOperation limitResults = Aggregation.limit(15);
        Aggregation aggregation = Aggregation.newAggregation(
                matchTime,
                groupByEndpoint,
                sortByCount,
                limitResults
        );

        AggregationResults<StatisticItem> results = mongoTemplate.aggregate(aggregation, "access_logs", StatisticItem.class);

        // Đảm bảo rằng mỗi item có name được đặt đúng
        return results.getMappedResults().stream()
                .peek(item -> {
                    // Đặt tên hiển thị bằng ID nếu name là null
                    if (item.getName() == null) {
                        item.setName(item.getId());
                    }
                }).toList();
    }

    private List<StatisticItem> getTopUsers(Timestamp start, Timestamp end) {
        // Tạo điều kiện thời gian
        MatchOperation matchTime = buildMatchOperationTime(start, end);

        // Nhóm theo userId và đếm, lấy username
        GroupOperation groupByUser = Aggregation
                .group("userId")
                .count().as("count")
                .first("username").as("username")
                .avg("responseTimeMs").as("avgResponseTime");

        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "count");

        // Giới hạn số lượng kết quả
        LimitOperation limitResults = Aggregation.limit(10);

        // Tạo aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchTime,
                groupByUser,
                sortByCount,
                limitResults
        );

        // Thực thi aggregation
        AggregationResults<StatisticItem> results = mongoTemplate.aggregate(aggregation, "access_logs", StatisticItem.class);
        return results.getMappedResults().stream()
                .peek(item -> {
                    // Đặt tên hiển thị bằng ID nếu name là null
                    if (item.getName() == null) {
                        item.setName(item.getUsername());
                    }
                })
                .toList();
    }
}
