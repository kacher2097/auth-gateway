package com.authenhub.service;

import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;
import com.authenhub.entity.mongo.AccessLog;
import com.authenhub.repository.AccessLogRepository;
import com.authenhub.service.interfaces.IAccessLogService;
import com.authenhub.utils.MongoQueryUtils;
import com.authenhub.utils.TimestampUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogService implements IAccessLogService {

    private final MongoTemplate mongoTemplate;
    private final AccessLogRepository accessLogRepository;

    @Override
    public StatisticGetResponse getAccessStats(StatisticSearchRequest request) {
        // If no date range provided, default to last 30 days
        if (request.getStartDate() == null) {
            request.setStartDate(TimestampUtils.addDays(TimestampUtils.now(), -30));
            request.setEndDate(TimestampUtils.now());
        }

        Timestamp start = request.getStartDate();
        Timestamp end = request.getEndDate();

        // Get total visits
        long totalVisits = accessLogRepository.countByDateRange(start, end);

        // Get daily visits
        Long dailyVisitsCount = countByDay(start, end);

        // Get all stats in a simplified way
        List<StatisticGetResponse.StatisticItem> browserStatsResponse = convertToStatItems(countByBrowser(start, end), "browser", "_id", null);
        List<StatisticGetResponse.StatisticItem> deviceStatsResponse = convertToStatItems(countByDeviceType(start, end), "device", "_id", null);
        List<StatisticGetResponse.StatisticItem> endpointStatsResponse = convertToStatItems(getTopEndpoints(start, end), "endpoint", "_id", "avgResponseTime");
        List<StatisticGetResponse.StatisticItem> userStatsResponse = convertToStatItems(getTopUsers(start, end), "user", "username", null);

        // Get login statistics
        long totalLogins = accessLogRepository.countByEndpointContaining(start, end, "/auth/login");
        long successfulLogins = accessLogRepository.countByEndpointAndStatusCode(start, end, "/auth/login", 200);
        long failedLogins = totalLogins - successfulLogins;

        // Build and return the complete response
        return StatisticGetResponse.builder()
                .totalVisits(totalVisits)
                .dailyVisits(dailyVisitsCount)
                .browserStats(browserStatsResponse)
                .deviceStats(deviceStatsResponse)
                .topEndpoints(endpointStatsResponse)
                .topUsers(userStatsResponse)
                .totalLogins(totalLogins)
                .successfulLogins(successfulLogins)
                .failedLogins(failedLogins)
                .build();
    }

    @Override
    public long countTotalVisits(Timestamp start, Timestamp end) {

        return 0;
    }

    @Override
    public List<Map<String, Object>> getLoginActivity(Timestamp start, Timestamp end) {
        // If no date range provided, default to last 30 days
        if (start == null) {
            end = TimestampUtils.now();
            start = TimestampUtils.addDays(end, -30);
        }

        // Get login activity logs
        List<AccessLog> loginLogs = accessLogRepository.findByTimestampBetween(start, end);

        // Convert to response format
        List<Map<String, Object>> loginActivity = new ArrayList<>();

        for (AccessLog log : loginLogs) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", log.getId());
            activity.put("username", log.getUsername() != null ? log.getUsername() : "anonymous");
            activity.put("ip", log.getIpAddress());
            activity.put("status", log.getStatusCode() == 200 ? "success" : "failed");
            activity.put("timestamp", log.getTimestamp().toString());
            activity.put("userAgent", log.getUserAgent());

            if (log.getStatusCode() != 200) {
                activity.put("reason", "Authentication failed");
            }

            loginActivity.add(activity);
        }

        return loginActivity;
    }

    /**
     * Count access logs by day
     *
     * @param start the start date
     * @param end   the end date
     * @return daily counts
     */
    @Override
    public Long countByDay(Timestamp start, Timestamp end) {
        Criteria condition = buildConditionSearch("timestamp", start, end);
        Query query = MongoQueryUtils.toQuery(condition);
        return mongoTemplate.count(query, "access_logs");
    }

    /**
     * Count access logs by browser
     *
     * @param start the start date
     * @param end   the end date
     * @return the list of browser counts
     */
    @Override
    public List<Map<String, Object>> countByBrowser(Timestamp start, Timestamp end) {

        MatchOperation matchOperation = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));
        GroupOperation groupOperation = Aggregation.group("browser")
                .count().as("count");

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "count");

        // Create aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                sortOperation
        );

        // Execute aggregation
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "access_logs", Map.class);

        // Convert results to List<Map<String, Object>>
        List<Map<String, Object>> browserCounts = new ArrayList<>();
        for (Map result : results.getMappedResults()) {
            Map<String, Object> browserCount = new HashMap<>();
            browserCount.put("_id", result.get("_id"));
            browserCount.put("count", result.get("count"));
            browserCounts.add(browserCount);
        }

        return browserCounts;
    }

    /**
     * Count access logs by device type
     *
     * @param start the start date
     * @param end   the end date
     * @return the list of device type counts
     */
    @Override
    public List<Map<String, Object>> countByDeviceType(Timestamp start, Timestamp end) {
        // Create match operation
        MatchOperation matchOperation = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Create group operation
        GroupOperation groupOperation = Aggregation.group("deviceType")
                .count().as("count");

        // Create sort operation
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "count");

        // Create aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                sortOperation
        );

        // Execute aggregation
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "access_logs", Map.class);

        // Convert results to List<Map<String, Object>>
        List<Map<String, Object>> deviceTypeCounts = new ArrayList<>();
        for (Map result : results.getMappedResults()) {
            Map<String, Object> deviceTypeCount = new HashMap<>();
            deviceTypeCount.put("_id", result.get("_id"));
            deviceTypeCount.put("count", result.get("count"));
            deviceTypeCounts.add(deviceTypeCount);
        }

        return deviceTypeCounts;
    }

    /**
     * Get top endpoints
     *
     * @param start the start date
     * @param end   the end date
     * @return the list of top endpoints
     */
    @Override
    public List<Map<String, Object>> getTopEndpoints(Timestamp start, Timestamp end) {
        // Create match operation
        MatchOperation matchOperation = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Create group operation
        GroupOperation groupOperation = Aggregation.group("endpoint")
                .count().as("count")
                .avg("responseTimeMs").as("avgResponseTime");

        // Create sort operation
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "count");

        // Create limit operation
        AggregationOperation limitOperation = Aggregation.limit(10);

        // Create aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                sortOperation,
                limitOperation
        );

        // Execute aggregation
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "access_logs", Map.class);

        // Convert results to List<Map<String, Object>>
        List<Map<String, Object>> topEndpoints = new ArrayList<>();
        for (Map result : results.getMappedResults()) {
            Map<String, Object> endpoint = new HashMap<>();
            endpoint.put("_id", result.get("_id"));
            endpoint.put("count", result.get("count"));
            endpoint.put("avgResponseTime", result.get("avgResponseTime"));
            topEndpoints.add(endpoint);
        }

        return topEndpoints;
    }

    /**
     * Get top users
     *
     * @param start the start date
     * @param end   the end date
     * @return the list of top users
     */
    @Override
    public List<Map<String, Object>> getTopUsers(Timestamp start, Timestamp end) {
        // Create match operation
        MatchOperation matchOperation = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

        // Create group operation
        GroupOperation groupOperation = Aggregation.group("userId")
                .count().as("count")
                .first("username").as("username");

        // Create sort operation
        SortOperation sortOperation = Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "count");

        // Create limit operation
        AggregationOperation limitOperation = Aggregation.limit(10);

        // Create aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                sortOperation,
                limitOperation
        );

        // Execute aggregation
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "access_logs", Map.class);

        // Convert results to List<Map<String, Object>>
        List<Map<String, Object>> topUsers = new ArrayList<>();
        for (Map result : results.getMappedResults()) {
            Map<String, Object> user = new HashMap<>();
            user.put("_id", result.get("_id"));
            user.put("count", result.get("count"));
            user.put("username", result.get("username"));
            topUsers.add(user);
        }

        return topUsers;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void parseUserAgent(AccessLog accessLog) {
        String userAgent = accessLog.getUserAgent();
        if (userAgent == null) {
            return;
        }

        // Simple parsing - in a real app you might use a library like UADetector or user-agent-utils
        userAgent = userAgent.toLowerCase();

        // Detect browser
        if (userAgent.contains("firefox")) {
            accessLog.setBrowser("Firefox");
        } else if (userAgent.contains("chrome") && !userAgent.contains("edge")) {
            accessLog.setBrowser("Chrome");
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            accessLog.setBrowser("Safari");
        } else if (userAgent.contains("edge") || userAgent.contains("edg")) {
            accessLog.setBrowser("Edge");
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            accessLog.setBrowser("Opera");
        } else {
            accessLog.setBrowser("Other");
        }

        // Detect OS
        if (userAgent.contains("windows")) {
            accessLog.setOperatingSystem("Windows");
        } else if (userAgent.contains("mac os")) {
            accessLog.setOperatingSystem("MacOS");
        } else if (userAgent.contains("linux")) {
            accessLog.setOperatingSystem("Linux");
        } else if (userAgent.contains("android")) {
            accessLog.setOperatingSystem("Android");
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            accessLog.setOperatingSystem("iOS");
        } else {
            accessLog.setOperatingSystem("Other");
        }

        // Detect device type
        if (userAgent.contains("mobile") || userAgent.contains("iphone")) {
            accessLog.setDeviceType("MOBILE");
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            accessLog.setDeviceType("TABLET");
        } else {
            accessLog.setDeviceType("DESKTOP");
        }
    }

    private Criteria buildConditionSearch(String key, Timestamp start, Timestamp end) {
        Criteria spectTime = MongoQueryUtils.dateRange("timestamp", start, end);
        return MongoQueryUtils.and(spectTime);
    }

    private Criteria buildConditionTotal(String key, String value) {
        return MongoQueryUtils.hasValue(key, value);
    }

    /**
     * Utility method to convert a list of maps to a list of StatisticItem objects
     *
     * @param dataList             the list of maps containing the data
     * @param itemType             the type of the statistic item
     * @param nameField            the field name to use for the name property
     * @param avgResponseTimeField the field name to use for the avgResponseTime property (can be null)
     * @return a list of StatisticItem objects
     */
    private List<StatisticGetResponse.StatisticItem> convertToStatItems(
            List<Map<String, Object>> dataList,
            String itemType,
            String nameField,
            String avgResponseTimeField) {

        List<StatisticGetResponse.StatisticItem> result = new ArrayList<>();

        for (Map<String, Object> item : dataList) {
            // Get name value, defaulting to "Unknown" if null
            String name = "Unknown";
            if (item.get(nameField) != null) {
                name = item.get(nameField).toString();
            } else if (nameField.equals("_id") && item.get("_id") != null) {
                name = item.get("_id").toString();
            }

            // Get count value
            int count = Integer.parseInt(item.get("count").toString());

            // Get avgResponseTime if applicable
            Double avgResponseTime = null;
            if (avgResponseTimeField != null && item.containsKey(avgResponseTimeField)) {
                Object value = item.get(avgResponseTimeField);
                if (value != null) {
                    avgResponseTime = Double.parseDouble(value.toString());
                }
            }

            // Build and add the StatisticItem
            result.add(StatisticGetResponse.StatisticItem.builder()
                    .name(name)
                    .count(count)
                    .type(itemType)
                    .avgResponseTime(avgResponseTime)
                    .build());
        }

        return result;
    }
}
