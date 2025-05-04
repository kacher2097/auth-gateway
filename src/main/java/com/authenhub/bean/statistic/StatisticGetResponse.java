package com.authenhub.bean.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticGetResponse {
    // Basic stats
    private String name;
    private int count;
    private String type;

    // Total visits
    private long totalVisits;

    // Daily visits
    private Long dailyVisits;

    // Browser stats
    private List<StatisticItem> browserStats;

    // Device type stats
    private List<StatisticItem> deviceStats;

    // Top endpoints
    private List<StatisticItem> topEndpoints;

    // Top users
    private List<StatisticItem> topUsers;

    // Login statistics
    private long totalLogins;
    private long successfulLogins;
    private long failedLogins;

    // Inner class for statistic items
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticItem {
        private String name;
        private int count;
        private String type;
        private Double avgResponseTime; // For endpoints
    }
}
