package com.authenhub.bean.tool.webcrawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebPageItem {
    private String id;
    private String url;
    private String title;
    private Integer status;
    private String contentType;
    private String size;
    private String lastModified;
    private Integer depth;
    private String parentUrl;
    private String content;
    private String extractedData;
}
