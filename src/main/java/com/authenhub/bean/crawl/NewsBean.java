package com.authenhub.bean.crawl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsBean {

    private String index;
    private String title;
    private String link;
    private String description;
    private String content;
    private String thumbnail;
    private String allImg;
    private List<String> images;
}
