package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.facebook.stats.FacebookEngagementResponse;
import com.authenhub.bean.facebook.stats.FacebookPageStatsResponse;
import com.authenhub.bean.facebook.stats.FacebookPostStatsResponse;
import com.authenhub.service.interfaces.IFacebookStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/facebook/stats")
@RequiredArgsConstructor
public class FacebookStatsController {

    private final IFacebookStatsService statsService;

    @GetMapping("/post/{postId}")
    public ApiResponse<?> getPostStats(
            @PathVariable String postId,
            @RequestParam String userId) {
        FacebookPostStatsResponse stats = statsService.getPostStats(postId, userId);
        return ApiResponse.success(stats);
    }

    @GetMapping("/page/{pageId}")
    public ApiResponse<?> getPageStats(
            @PathVariable String pageId,
            @RequestParam String userId) {
        FacebookPageStatsResponse stats = statsService.getPageStats(pageId, userId);
        return ApiResponse.success(stats);
    }

    @GetMapping("/engagement")
    public ApiResponse<?> getEngagementData(
            @RequestParam String id,
            @RequestParam String type,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam String userId) {

        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime());

        FacebookEngagementResponse data = statsService.getEngagementData(id, type, startTimestamp, endTimestamp, userId);
        return ApiResponse.success(data);
    }
}
