package com.authenhub.bean.facebook.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookEngagementResponse {
    private String id; // page or post id
    private String type; // "page" or "post"
    private List<Map<String, Object>> dailyStats;
    private Map<String, Integer> totalStats;
}
