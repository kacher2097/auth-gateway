package com.authenhub.service;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.mongo.AccessLog;
import com.authenhub.repository.AccessLogRepository;
import com.authenhub.service.interfaces.IAccessLogService;
import com.authenhub.utils.TimestampUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseSwitcherConfig databaseConfig;
    private final AccessLogRepository accessLogRepository;

    @Override
    public Map<String, Object> getAccessStats(Timestamp start, Timestamp end) {
        Map<String, Object> stats = new HashMap<>();

        // If no date range provided, default to last 30 days
        if (start == null) {
            end = TimestampUtils.now();
            start = TimestampUtils.addDays(end, -30);
        }

        // Get total visits
        long totalVisits = accessLogRepository.countByDateRange(start, end);
        stats.put("totalVisits", totalVisits);

        // Get daily visits
        List<Map<String, Object>> dailyVisits = countByDay(start, end);
        stats.put("dailyVisits", dailyVisits);

        // Get browser stats
        List<Map<String, Object>> browserStats = countByBrowser(start, end);
        stats.put("browserStats", browserStats);

        // Get device type stats
        List<Map<String, Object>> deviceStats = countByDeviceType(start, end);
        stats.put("deviceStats", deviceStats);

        // Get top endpoints
        List<Map<String, Object>> topEndpoints = getTopEndpoints(start, end);
        stats.put("topEndpoints", topEndpoints);

        // Get top users
        List<Map<String, Object>> topUsers = getTopUsers(start, end);
        stats.put("topUsers", topUsers);

        // Get login statistics
        long totalLogins = accessLogRepository.countByEndpointContaining(start, end, "/auth/login");
        long successfulLogins = accessLogRepository.countByEndpointAndStatusCode(start, end, "/auth/login", 200);
        long failedLogins = totalLogins - successfulLogins;

        stats.put("totalLogins", totalLogins);
        stats.put("successfulLogins", successfulLogins);
        stats.put("failedLogins", failedLogins);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getLoginActivity(Timestamp start, Timestamp end) {
        // If no date range provided, default to last 30 days
        if (start == null) {
            end = TimestampUtils.now();
            start = TimestampUtils.addDays(end, -30);
        }

        // Get login activity logs
        List<AccessLog> loginLogs = accessLogRepository.findByEndpointContainingAndTimestampBetween("/auth/login", start, end);

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
     * @return the list of daily counts
     */
    @Override
    public List<Map<String, Object>> countByDay(Timestamp start, Timestamp end) {
        if (databaseConfig.isMongoActive()) {
            // Create match operation
            MatchOperation matchOperation = Aggregation.match(Criteria.where("timestamp").gte(start).lte(end));

            // Create projection operation to extract date
            ProjectionOperation projectOperation = Aggregation.project()
                    .andExpression("timestamp").as("date");

            // Create sort operation
            SortOperation sortOperation = Aggregation.sort(org.springframework.data.domain.Sort.Direction.ASC, "_id");

            // Create aggregation
            Aggregation aggregation = Aggregation.newAggregation(
                    matchOperation,
                    projectOperation,
                    sortOperation
            );

            // Execute aggregation
            AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "access_logs", Map.class);

            // Convert results to List<Map<String, Object>>
            List<Map<String, Object>> dailyCounts = new ArrayList<>();
            for (Map result : results.getMappedResults()) {
                Map<String, Object> dailyCount = new HashMap<>();
                dailyCount.put("_id", result.get("_id"));
                dailyCount.put("count", result.get("count"));
                dailyCounts.add(dailyCount);
            }

            return dailyCounts;
        } else {
            // PostgreSQL implementation
            String sql = ""
                    + "SELECT TO_CHAR(timestamp, 'YYYY-MM-DD') as date, COUNT(*) as count "
                    + "FROM access_logs "
                    + "WHERE timestamp BETWEEN ? AND ? "
                    + "GROUP BY TO_CHAR(timestamp, 'YYYY-MM-DD') "
                    + "ORDER BY date ASC";

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> dayCount = new HashMap<>();
                dayCount.put("date", rs.getString("date"));
                dayCount.put("count", rs.getLong("count"));
                return dayCount;
            }, start, end);
        }
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

}
