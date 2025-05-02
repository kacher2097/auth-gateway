package com.authenhub.bean.tool.webcrawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebCrawlerRequest {
    private String userId;
    private String url;
    private Integer depth;
    private Integer maxPages;
    private String includePattern;
    private String excludePattern;
    private String userAgent;
    private Integer timeout;
    private String[] contentTypes;
    private String cssSelector;
    private String xpath;
    private Boolean respectRobotsTxt;
    private String scheduleType; // once, daily, weekly
    private String scheduleTime;
}
