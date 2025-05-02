package com.authenhub.bean.tool.blogcrawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCrawlerResponse {
    private List<BlogPostItem> posts;
    private int totalCount;
    private String sourceUrl;
    private String crawlDate;
}
