package com.authenhub.bean.facebook.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPageStatsResponse {
    private String pageId;
    private int fanCount;
    private int newFans;
    private int postCount;
    private int totalReach;
    private int totalEngagement;
    private Timestamp timestamp;
}
