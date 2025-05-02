package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.blogcrawler.BlogCrawlerRequest;
import com.authenhub.bean.tool.blogcrawler.BlogCrawlerResponse;
import com.authenhub.service.interfaces.IBlogCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/blog-crawler")
@RequiredArgsConstructor
public class BlogCrawlerController {

    private final IBlogCrawlerService blogCrawlerService;

    /**
     * Crawl blog posts from a website
     * @param request The blog crawler request
     * @return The blog crawler response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BlogCrawlerResponse>> crawlBlogPosts(@RequestBody BlogCrawlerRequest request) {
        BlogCrawlerResponse response = blogCrawlerService.crawlBlogPosts(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Export blog posts to a file
     * @param format The export format (csv, json, excel)
     *
     * @return The file content
     */
    @PostMapping("/export/{format}")
    public ResponseEntity<byte[]> exportBlogPosts(
            @PathVariable String format,
            @RequestBody BlogCrawlerResponse response) {
        
        byte[] fileContent = blogCrawlerService.exportBlogPosts(format, response);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "blog-posts." + format);
        
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
    
    private MediaType getMediaType(String format) {
        switch (format.toLowerCase()) {
            case "json":
                return MediaType.APPLICATION_JSON;
            case "csv":
                return MediaType.parseMediaType("text/csv");
            case "excel":
                return MediaType.parseMediaType("application/vnd.ms-excel");
            default:
                return MediaType.TEXT_PLAIN;
        }
    }
}
