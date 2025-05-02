package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.blogcrawler.BlogCrawlerRequest;
import com.authenhub.bean.tool.blogcrawler.BlogCrawlerResponse;

public interface IBlogCrawlerService {
    /**
     * Crawl blog posts from a website
     * @param request The blog crawler request
     * @return The blog crawler response containing the crawled posts
     */
    BlogCrawlerResponse crawlBlogPosts(BlogCrawlerRequest request);
    
    /**
     * Export blog posts to a file
     * @param format The export format (csv, json, excel)
     * @param blogCrawlerResponse The blog crawler response to export
     * @return The file content as byte array
     */
    byte[] exportBlogPosts(String format, BlogCrawlerResponse blogCrawlerResponse);
}
