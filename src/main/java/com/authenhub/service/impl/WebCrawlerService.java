package com.authenhub.service.impl;

import com.authenhub.bean.tool.webcrawler.WebCrawlerRequest;
import com.authenhub.bean.tool.webcrawler.WebCrawlerResponse;
import com.authenhub.bean.tool.webcrawler.WebPageItem;
import com.authenhub.entity.WebCrawlerJob;
import com.authenhub.repository.WebCrawlerJobRepository;
import com.authenhub.service.interfaces.IWebCrawlerService;
import com.authenhub.utils.TimestampUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebCrawlerService implements IWebCrawlerService {

    private final WebCrawlerJobRepository webCrawlerJobRepository;
    private final ObjectMapper objectMapper;

    @Override
    public WebCrawlerResponse crawlWebPages(WebCrawlerRequest request) {
        log.info("Crawling web pages from URL: {}", request.getUrl());

        // Create a new job
        WebCrawlerJob job = WebCrawlerJob.builder()
                .userId(request.getUserId())
                .url(request.getUrl())
                .depth(request.getDepth())
                .maxPages(request.getMaxPages())
                .includePattern(request.getIncludePattern())
                .excludePattern(request.getExcludePattern())
                .userAgent(request.getUserAgent())
                .timeout(request.getTimeout())
                .contentTypes(request.getContentTypes() != null ? String.join(",", request.getContentTypes()) : null)
                .cssSelector(request.getCssSelector())
                .xpath(request.getXpath())
                .respectRobotsTxt(request.getRespectRobotsTxt())
                .createdAt(TimestampUtils.now())
                .status("RUNNING")
                .build();

        job = webCrawlerJobRepository.save(job);

        try {
            // TODO: Implement actual crawling logic using JSoup, Selenium, or similar library

            // For now, return mock data
            List<WebPageItem> pages = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                pages.add(WebPageItem.builder()
                        .id(UUID.randomUUID().toString())
                        .url(request.getUrl() + "/page-" + i)
                        .title("Sample Page " + i)
                        .status(200)
                        .contentType("text/html")
                        .size((i * 10) + " KB")
                        .lastModified("2023-05-" + (15 - i))
                        .depth(1)
                        .parentUrl(request.getUrl())
                        .content("<html><body><h1>Sample Page " + i + "</h1></body></html>")
                        .extractedData("Sample extracted data " + i)
                        .build());
            }

            String jobId = job.getId().toString();

            WebCrawlerResponse response = WebCrawlerResponse.builder()
                    .pages(pages)
                    .totalCount(pages.size())
                    .baseUrl(request.getUrl())
                    .crawlDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .crawlDuration("2.5 seconds")
                    .jobId(jobId)
                    .status("completed")
                    .build();

            // Update job with results
            job.setCompletedAt(TimestampUtils.now());
            job.setStatus("COMPLETED");
            job.setTotalPages(pages.size());
            job.setCrawlDuration("2.5 seconds");
            job.setResultJson(objectMapper.writeValueAsString(response));
            webCrawlerJobRepository.save(job);

            return response;
        } catch (Exception e) {
            log.error("Error crawling web pages", e);

            // Update job with error
            job.setCompletedAt(TimestampUtils.now());
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
            webCrawlerJobRepository.save(job);

            throw new RuntimeException("Failed to crawl web pages: " + e.getMessage(), e);
        }
    }

    @Override
    public WebCrawlerResponse getCrawlStatus(String jobId) {
        log.info("Getting crawl status for job ID: {}", jobId);

        try {
            Long id = Long.parseLong(jobId);
            WebCrawlerJob job = webCrawlerJobRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Web crawler job not found with ID: " + jobId));

            if (job.getResultJson() != null && !job.getResultJson().isEmpty()) {
                return objectMapper.readValue(job.getResultJson(), WebCrawlerResponse.class);
            }

            // If no result JSON yet, return a basic response
            return WebCrawlerResponse.builder()
                    .jobId(jobId)
                    .status(job.getStatus())
                    .baseUrl(job.getUrl())
                    .crawlDate(job.getCreatedAt().toString())
                    .build();
        } catch (Exception e) {
            log.error("Error getting crawl status", e);

            // Return a not found response
            return WebCrawlerResponse.builder()
                    .jobId(jobId)
                    .status("not_found")
                    .build();
        }
    }

    @Override
    public byte[] exportWebPages(String format, String jobId) {
        log.info("Exporting web pages for job ID: {} to format: {}", jobId, format);

        // Get the crawl response
        WebCrawlerResponse response = getCrawlStatus(jobId);
        if ("not_found".equals(response.getStatus())) {
            return "Crawl job not found".getBytes();
        }

        // TODO: Implement actual export logic based on the format

        // For now, return mock data
        StringBuilder sb = new StringBuilder();

        if ("json".equalsIgnoreCase(format)) {
            sb.append("{\n");
            sb.append("  \"pages\": [\n");
            for (int i = 0; i < response.getPages().size(); i++) {
                WebPageItem page = response.getPages().get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(page.getId()).append("\",\n");
                sb.append("      \"url\": \"").append(page.getUrl()).append("\",\n");
                sb.append("      \"title\": \"").append(page.getTitle()).append("\",\n");
                sb.append("      \"status\": ").append(page.getStatus()).append(",\n");
                sb.append("      \"contentType\": \"").append(page.getContentType()).append("\",\n");
                sb.append("      \"size\": \"").append(page.getSize()).append("\"\n");
                sb.append("    }");
                if (i < response.getPages().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ],\n");
            sb.append("  \"totalCount\": ").append(response.getTotalCount()).append(",\n");
            sb.append("  \"baseUrl\": \"").append(response.getBaseUrl()).append("\",\n");
            sb.append("  \"crawlDate\": \"").append(response.getCrawlDate()).append("\",\n");
            sb.append("  \"crawlDuration\": \"").append(response.getCrawlDuration()).append("\",\n");
            sb.append("  \"jobId\": \"").append(response.getJobId()).append("\",\n");
            sb.append("  \"status\": \"").append(response.getStatus()).append("\"\n");
            sb.append("}");
        } else if ("csv".equalsIgnoreCase(format)) {
            sb.append("id,url,title,status,contentType,size\n");
            for (WebPageItem page : response.getPages()) {
                sb.append(page.getId()).append(",");
                sb.append("\"").append(page.getUrl()).append("\",");
                sb.append("\"").append(page.getTitle()).append("\",");
                sb.append(page.getStatus()).append(",");
                sb.append("\"").append(page.getContentType()).append("\",");
                sb.append("\"").append(page.getSize()).append("\"\n");
            }
        } else {
            // Default to plain text
            sb.append("Web Pages from ").append(response.getBaseUrl()).append("\n");
            sb.append("Crawled on: ").append(response.getCrawlDate()).append("\n");
            sb.append("Duration: ").append(response.getCrawlDuration()).append("\n\n");

            for (WebPageItem page : response.getPages()) {
                sb.append("Title: ").append(page.getTitle()).append("\n");
                sb.append("URL: ").append(page.getUrl()).append("\n");
                sb.append("Status: ").append(page.getStatus()).append("\n");
                sb.append("Content Type: ").append(page.getContentType()).append("\n");
                sb.append("Size: ").append(page.getSize()).append("\n\n");
            }
        }

        return sb.toString().getBytes();
    }

    @Override
    public String scheduleCrawlJob(WebCrawlerRequest request) {
        log.info("Scheduling crawl job for URL: {}", request.getUrl());

        // Parse schedule time
        Timestamp nextRunAt = null;
        if ("once".equals(request.getScheduleType())) {
            // Schedule for a specific time
            if (request.getScheduleTime() != null && !request.getScheduleTime().isEmpty()) {
                // Convert ISO string to Timestamp
                try {
                    nextRunAt = Timestamp.valueOf(request.getScheduleTime().replace('T', ' ').substring(0, 19));
                } catch (Exception e) {
                    nextRunAt = TimestampUtils.now();
                }
            } else {
                // If no time specified, schedule for now
                nextRunAt = TimestampUtils.now();
            }
        } else if ("daily".equals(request.getScheduleType())) {
            // Schedule for tomorrow at the same time (add 24 hours)
            nextRunAt = TimestampUtils.addHours(TimestampUtils.now(), 24);
        } else if ("weekly".equals(request.getScheduleType())) {
            // Schedule for next week at the same time (add 7 days)
            nextRunAt = TimestampUtils.addDays(TimestampUtils.now(), 7);
        } else {
            // Default to now
            nextRunAt = TimestampUtils.now();
        }

        // Create a new job
        WebCrawlerJob job = WebCrawlerJob.builder()
                .userId(request.getUserId())
                .url(request.getUrl())
                .depth(request.getDepth())
                .maxPages(request.getMaxPages())
                .includePattern(request.getIncludePattern())
                .excludePattern(request.getExcludePattern())
                .userAgent(request.getUserAgent())
                .timeout(request.getTimeout())
                .contentTypes(request.getContentTypes() != null ? String.join(",", request.getContentTypes()) : null)
                .cssSelector(request.getCssSelector())
                .xpath(request.getXpath())
                .respectRobotsTxt(request.getRespectRobotsTxt())
                .scheduleType(request.getScheduleType())
                .scheduleTime(request.getScheduleTime())
                .createdAt(TimestampUtils.now())
                .nextRunAt(nextRunAt)
                .status("SCHEDULED")
                .build();

        job = webCrawlerJobRepository.save(job);

        return job.getId().toString();
    }

    public List<WebCrawlerJob> getUserJobs(String userId) {
        return webCrawlerJobRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public WebCrawlerJob getJobById(Long jobId) {
        return webCrawlerJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Web crawler job not found with ID: " + jobId));
    }

    public List<WebCrawlerJob> getScheduledJobs() {
        return webCrawlerJobRepository.findByStatusAndNextRunAtLessThanEqual("SCHEDULED", TimestampUtils.now());
    }
}
