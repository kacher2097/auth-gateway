package com.authenhub.service.impl;

import com.authenhub.bean.tool.blogcrawler.BlogCrawlerRequest;
import com.authenhub.bean.tool.blogcrawler.BlogCrawlerResponse;
import com.authenhub.bean.tool.blogcrawler.BlogPostItem;
import com.authenhub.entity.BlogCrawlerJob;
import com.authenhub.repository.BlogCrawlerJobRepository;
import com.authenhub.service.interfaces.IBlogCrawlerService;
import com.authenhub.utils.TimestampUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogCrawlerService implements IBlogCrawlerService {

    private final BlogCrawlerJobRepository blogCrawlerJobRepository;
    private final ObjectMapper objectMapper;

    @Override
    public BlogCrawlerResponse crawlBlogPosts(BlogCrawlerRequest request) {
        log.info("Crawling blog posts from URL: {}", request.getUrl());
        
        // Create a new job
        BlogCrawlerJob job = BlogCrawlerJob.builder()
                .userId(request.getUserId())
                .url(request.getUrl())
                .category(request.getCategory())
                .searchKeyword(request.getSearchKeyword())
                .dateFrom(request.getDateFrom())
                .dateTo(request.getDateTo())
                .author(request.getAuthor())
                .sortBy(request.getSortBy())
                .sortOrder(request.getSortOrder())
                .createdAt(TimestampUtils.now())
                .status("RUNNING")
                .build();
        
        job = blogCrawlerJobRepository.save(job);
        
        try {
            // TODO: Implement actual crawling logic using JSoup or similar library
            
            // For now, return mock data
            List<BlogPostItem> posts = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                posts.add(BlogPostItem.builder()
                        .id(UUID.randomUUID().toString())
                        .title("Sample Blog Post " + i)
                        .url(request.getUrl() + "/post-" + i)
                        .date("2023-05-" + (15 - i))
                        .author("Author " + i)
                        .category(request.getCategory() != null ? request.getCategory() : "Technology")
                        .excerpt("This is a sample blog post excerpt for demonstration purposes...")
                        .imageUrl("https://example.com/images/post-" + i + ".jpg")
                        .build());
            }
            
            BlogCrawlerResponse response = BlogCrawlerResponse.builder()
                    .posts(posts)
                    .totalCount(posts.size())
                    .sourceUrl(request.getUrl())
                    .crawlDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
            
            // Update job with results
            job.setCompletedAt(TimestampUtils.now());
            job.setStatus("COMPLETED");
            job.setTotalPosts(posts.size());
            job.setResultJson(objectMapper.writeValueAsString(response));
            blogCrawlerJobRepository.save(job);
            
            return response;
        } catch (Exception e) {
            log.error("Error crawling blog posts", e);
            
            // Update job with error
            job.setCompletedAt(TimestampUtils.now());
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
            blogCrawlerJobRepository.save(job);
            
            throw new RuntimeException("Failed to crawl blog posts: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] exportBlogPosts(String format, BlogCrawlerResponse blogCrawlerResponse) {
        log.info("Exporting blog posts to format: {}", format);
        
        // TODO: Implement actual export logic based on the format
        
        // For now, return mock data
        StringBuilder sb = new StringBuilder();
        
        if ("json".equalsIgnoreCase(format)) {
            sb.append("{\n");
            sb.append("  \"posts\": [\n");
            for (int i = 0; i < blogCrawlerResponse.getPosts().size(); i++) {
                BlogPostItem post = blogCrawlerResponse.getPosts().get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(post.getId()).append("\",\n");
                sb.append("      \"title\": \"").append(post.getTitle()).append("\",\n");
                sb.append("      \"url\": \"").append(post.getUrl()).append("\",\n");
                sb.append("      \"date\": \"").append(post.getDate()).append("\",\n");
                sb.append("      \"author\": \"").append(post.getAuthor()).append("\",\n");
                sb.append("      \"category\": \"").append(post.getCategory()).append("\"\n");
                sb.append("    }");
                if (i < blogCrawlerResponse.getPosts().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ],\n");
            sb.append("  \"totalCount\": ").append(blogCrawlerResponse.getTotalCount()).append(",\n");
            sb.append("  \"sourceUrl\": \"").append(blogCrawlerResponse.getSourceUrl()).append("\",\n");
            sb.append("  \"crawlDate\": \"").append(blogCrawlerResponse.getCrawlDate()).append("\"\n");
            sb.append("}");
        } else if ("csv".equalsIgnoreCase(format)) {
            sb.append("id,title,url,date,author,category\n");
            for (BlogPostItem post : blogCrawlerResponse.getPosts()) {
                sb.append(post.getId()).append(",");
                sb.append("\"").append(post.getTitle()).append("\",");
                sb.append("\"").append(post.getUrl()).append("\",");
                sb.append(post.getDate()).append(",");
                sb.append("\"").append(post.getAuthor()).append("\",");
                sb.append("\"").append(post.getCategory()).append("\"\n");
            }
        } else {
            // Default to plain text
            sb.append("Blog Posts from ").append(blogCrawlerResponse.getSourceUrl()).append("\n");
            sb.append("Crawled on: ").append(blogCrawlerResponse.getCrawlDate()).append("\n\n");
            
            for (BlogPostItem post : blogCrawlerResponse.getPosts()) {
                sb.append("Title: ").append(post.getTitle()).append("\n");
                sb.append("URL: ").append(post.getUrl()).append("\n");
                sb.append("Date: ").append(post.getDate()).append("\n");
                sb.append("Author: ").append(post.getAuthor()).append("\n");
                sb.append("Category: ").append(post.getCategory()).append("\n\n");
            }
        }
        
        return sb.toString().getBytes();
    }
    
    public List<BlogCrawlerJob> getUserJobs(String userId) {
        return blogCrawlerJobRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public BlogCrawlerJob getJobById(Long jobId) {
        return blogCrawlerJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Blog crawler job not found with ID: " + jobId));
    }
}
