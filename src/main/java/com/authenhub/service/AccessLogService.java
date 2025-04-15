package com.authenhub.service;

import com.authenhub.dto.AccessLogAggregationDto;
import com.authenhub.entity.AccessLog;
import com.authenhub.repository.AccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    public void logAccess(HttpServletRequest request, int statusCode, long startTime) {
        try {
            AccessLog accessLog = new AccessLog();

            // Set basic request info
            accessLog.setIpAddress(getClientIp(request));
            accessLog.setUserAgent(request.getHeader("User-Agent"));
            accessLog.setEndpoint(request.getRequestURI());
            accessLog.setMethod(request.getMethod());
            accessLog.setStatusCode(statusCode);
            accessLog.setTimestamp(LocalDateTime.now());
//            accessLog.setSessionId(request.getSession().getId());
            accessLog.setReferrer(request.getHeader("Referer"));
            accessLog.setResponseTimeMs(System.currentTimeMillis() - startTime);

            // Set user info if authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                accessLog.setUsername(auth.getName());
                // Note: userId would need to be extracted from your custom UserDetails implementation
            }

            // Parse user agent for browser, OS, device type
            parseUserAgent(accessLog);

            // Save the log
            accessLogRepository.save(accessLog);
        } catch (Exception e) {
            log.error("Error logging access", e);
        }
    }

    public Map<String, Object> getAccessStats(LocalDateTime start, LocalDateTime end) {
        try {

            log.info("Getting access stats for date range: {} to {}", start, end);
            Map<String, Object> stats = new HashMap<>();

            // If no date range provided, default to last 30 days
            if (start == null) {
                end = LocalDateTime.now();
                start = end.minus(30, ChronoUnit.DAYS);
            }

            // Get total visits
            long totalVisits = accessLogRepository.countByDateRange(start, end);
            stats.put("totalVisits", totalVisits);

            // Get daily visits
            List<AccessLogAggregationDto.DailyCount> dailyVisits = accessLogRepository.countByDay(start, end);
            stats.put("dailyVisits", dailyVisits);

            // Get browser stats
            List<AccessLogAggregationDto.BrowserCount> browserStats = accessLogRepository.countByBrowser(start, end);
            stats.put("browserStats", browserStats);

            // Get device type stats
            List<AccessLogAggregationDto.DeviceTypeCount> deviceStats = accessLogRepository.countByDeviceType(start, end);
            stats.put("deviceStats", deviceStats);

            // Get top endpoints
            List<AccessLogAggregationDto.EndpointStats> topEndpoints = accessLogRepository.getTopEndpoints(start, end);
            stats.put("topEndpoints", topEndpoints);

            // Get top users
            List<AccessLogAggregationDto.UserStats> topUsers = accessLogRepository.getTopUsers(start, end);
            stats.put("topUsers", topUsers);

            return stats;
        } catch (Exception e) {
            log.error("Error getting access stats", e);
            return Collections.emptyMap();
        }

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
