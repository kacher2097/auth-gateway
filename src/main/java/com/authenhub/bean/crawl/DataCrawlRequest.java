package com.authenhub.bean.crawl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCrawlRequest {
    private String source;
    private String siteUrl;
    private String categoryUri;
    private String token;
    private String username;
    private String password;
    private Integer limit;

    private Boolean isWriteSheets;
}
