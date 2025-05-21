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
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
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
    public ApiResponse<?> getTopEndpointsInternal(StatisticSearchRequest request) {
        return null;
    }

    @Override
    public ApiResponse<?> getTopUsersInternal(StatisticSearchRequest request) {
        return null;
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


    /**
     * Đếm số lượng đăng nhập theo trạng thái
     *
     * @param start      ngày bắt đầu
     * @param end        ngày kết thúc
     * @param statusCode mã trạng thái (null để đếm tất cả)
     * @return số lượng đăng nhập
     */
    private long countLoginsByStatus(Timestamp start, Timestamp end, Integer statusCode) {
        // Tạo điều kiện thời gian
        MatchOperation matchTime = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Tạo điều kiện endpoint
        MatchOperation matchEndpoint = Aggregation.match(Criteria.where("endpoint").is("/auth/login"));

        // Danh sách các thao tác
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(matchTime);
        operations.add(matchEndpoint);

        // Thêm điều kiện mã trạng thái nếu có
        if (statusCode != null) {
            operations.add(Aggregation.match(Criteria.where("statusCode").is(statusCode)));
        }

        // Thêm thao tác đếm
        operations.add(Aggregation.count().as("count"));

        // Tạo aggregation
        Aggregation aggregation = Aggregation.newAggregation(operations);

        // Thực thi aggregation
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "access_logs", Document.class);

        // Lấy kết quả
        Document result = results.getUniqueMappedResult();
        return result != null ? ((Number) result.get("count")).longValue() : 0;
    }

    /**
     * Lấy thống kê theo trình duyệt
     *
     * @param start ngày bắt đầu
     * @param end   ngày kết thúc
     * @return danh sách thống kê theo trình duyệt
     */
    private List<StatisticItem> getBrowserStats(Timestamp start, Timestamp end) {
        // Tạo điều kiện thời gian
        MatchOperation matchTime = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Nhóm theo trình duyệt và đếm
        GroupOperation groupByBrowser = Aggregation.group("browser")
                .count().as("count");

        // Sắp xếp theo số lượng giảm dần
        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "count");

        // Tạo aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchTime,
                groupByBrowser,
                sortByCount
        );

        // Thực thi aggregation
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "access_logs", Document.class);

        // Chuyển đổi kết quả thành danh sách StatisticItem
        return results.getMappedResults().stream()
                .map(doc -> StatisticItem.builder()
                        .name(doc.get("_id") != null ? doc.get("_id").toString() : "Unknown")
                        .count(((Number) doc.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Lấy thống kê theo loại thiết bị
     *
     * @param start ngày bắt đầu
     * @param end   ngày kết thúc
     * @return danh sách thống kê theo loại thiết bị
     */
    private List<StatisticItem> getDeviceTypeStats(Timestamp start, Timestamp end) {
        // Tạo điều kiện thời gian
        MatchOperation matchTime = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Nhóm theo loại thiết bị và đếm
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
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "access_logs", Document.class);

        // Chuyển đổi kết quả thành danh sách StatisticItem
        return results.getMappedResults().stream()
                .map(doc -> StatisticItem.builder()
                        .name(doc.get("_id") != null ? doc.get("_id").toString() : "Unknown")
                        .count(((Number) doc.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách endpoint phổ biến nhất
     *
     * @param start ngày bắt đầu
     * @param end   ngày kết thúc
     * @return danh sách endpoint phổ biến nhất
     */
    private List<StatisticItem> getTopEndpoints(Timestamp start, Timestamp end) {
        // Tạo điều kiện thời gian
        MatchOperation matchTime = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Nhóm theo endpoint và đếm, tính thời gian phản hồi trung bình
        GroupOperation groupByEndpoint = Aggregation.group("endpoint")
                .count().as("count")
                .avg("responseTimeMs").as("avgResponseTime");

        // Sắp xếp theo số lượng giảm dần
        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "count");

        // Giới hạn số lượng kết quả
        LimitOperation limitResults = Aggregation.limit(10);

        // Tạo aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchTime,
                groupByEndpoint,
                sortByCount,
                limitResults
        );

        // Thực thi aggregation
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "access_logs", Document.class);

        // Chuyển đổi kết quả thành danh sách StatisticItem
        return results.getMappedResults().stream()
                .map(doc -> {
                    Double avgResponseTime = null;
                    if (doc.get("avgResponseTime") != null) {
                        avgResponseTime = ((Number) doc.get("avgResponseTime")).doubleValue();
                    }

                    return StatisticItem.builder()
                            .name(doc.get("_id") != null ? doc.get("_id").toString() : "Unknown")
                            .count(((Number) doc.get("count")).longValue())
                            .avgResponseTime(avgResponseTime)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách người dùng hoạt động nhiều nhất
     *
     * @param start ngày bắt đầu
     * @param end   ngày kết thúc
     * @return danh sách người dùng hoạt động nhiều nhất
     */
    private List<StatisticItem> getTopUsers(Timestamp start, Timestamp end) {
        // Tạo điều kiện thời gian
        MatchOperation matchTime = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Nhóm theo userId và đếm, lấy username
        GroupOperation groupByUser = Aggregation.group("userId")
                .count().as("count")
                .first("username").as("username");

        // Sắp xếp theo số lượng giảm dần
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
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "access_logs", Document.class);

        // Chuyển đổi kết quả thành danh sách StatisticItem
        return results.getMappedResults().stream()
                .map(doc -> StatisticItem.builder()
                        .name(doc.get("username") != null ? doc.get("username").toString() :
                                (doc.get("_id") != null ? doc.get("_id").toString() : "Unknown"))
                        .count(((Number) doc.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }
}
