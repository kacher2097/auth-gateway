package com.authenhub.bean.tool.blogcrawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCrawlerRequest {
    private String userId;
    private String url;
    private String category;
    private Integer limit;
    private String searchKeyword;
    private String dateFrom;
    private String dateTo;
    private String author;
    private String sortBy;
    private String sortOrder;
}
