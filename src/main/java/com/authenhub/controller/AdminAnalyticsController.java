package com.authenhub.controller;

import com.authenhub.dto.AccessStatsRequest;
import com.authenhub.dto.ApiResponse;
import com.authenhub.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final AccessLogService accessLogService;

    @PostMapping("/access-stats")
    public ResponseEntity<ApiResponse> getAccessStats(@RequestBody AccessStatsRequest request) {
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }

        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        Map<String, Object> stats = accessLogService.getAccessStats(startDate, endDate);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("Access statistics retrieved successfully")
                .data(stats)
                .build();

        return ResponseEntity.ok(response);
    }
}
