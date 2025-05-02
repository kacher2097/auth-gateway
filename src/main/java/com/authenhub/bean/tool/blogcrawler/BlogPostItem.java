package com.authenhub.bean.tool.blogcrawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostItem {
    private String id;
    private String title;
    private String url;
    private String date;
    private String author;
    private String category;
    private String excerpt;
    private String imageUrl;
}
