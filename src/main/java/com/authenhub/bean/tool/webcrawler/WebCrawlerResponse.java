package com.authenhub.bean.tool.webcrawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebCrawlerResponse {
    private List<WebPageItem> pages;
    private int totalCount;
    private String baseUrl;
    private String crawlDate;
    private String crawlDuration;
    private String jobId;
    private String status;
}
