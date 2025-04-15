package com.authenhub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTOs for MongoDB aggregation results from AccessLogRepository
 */
public class AccessLogAggregationDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyCount {
        private String id; // Date in format YYYY-MM-DD
        private Long count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrowserCount {
        private String id; // Browser name
        private Long count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceTypeCount {
        private String id; // Device type
        private Long count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointStats {
        private String id; // Endpoint
        private Long count;
        private Double avgResponseTime;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private String id; // User ID
        private String username;
        private Long count;
    }
}
