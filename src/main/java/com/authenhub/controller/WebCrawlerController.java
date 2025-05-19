package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.webcrawler.WebCrawlerRequest;
import com.authenhub.bean.tool.webcrawler.WebCrawlerResponse;
import com.authenhub.service.interfaces.IWebCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/web-crawler")
@RequiredArgsConstructor
public class WebCrawlerController {

    private final IWebCrawlerService webCrawlerService;

    /**
     * Crawl web pages from a website
     * @param request The web crawler request
     * @return The web crawler response
     */
    @PostMapping
    public ApiResponse<?> crawlWebPages(@RequestBody WebCrawlerRequest request) {
        WebCrawlerResponse response = webCrawlerService.crawlWebPages(request);
        return ApiResponse.success(response);
    }

    /**
     * Get the status of a crawl job
     * @param jobId The job ID
     * @return The web crawler response
     */
    @GetMapping("/{jobId}")
    public ApiResponse<?> getCrawlStatus(@PathVariable String jobId) {
        WebCrawlerResponse response = webCrawlerService.getCrawlStatus(jobId);
        return ApiResponse.success(response);
    }

    /**
     * Export web pages to a file
     * @param format The export format (csv, json, excel)
     * @param jobId The job ID
     * @return The file content
     */
    @GetMapping("/export/{format}/{jobId}")
    public ResponseEntity<byte[]> exportWebPages(
            @PathVariable String format,
            @PathVariable String jobId) {

        byte[] fileContent = webCrawlerService.exportWebPages(format, jobId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "web-pages." + format);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    /**
     * Schedule a crawl job
     * @param request The web crawler request
     * @return The job ID
     */
    @PostMapping("/schedule")
    public ApiResponse<?> scheduleCrawlJob(@RequestBody WebCrawlerRequest request) {
        String jobId = webCrawlerService.scheduleCrawlJob(request);
        return ApiResponse.success(jobId);
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
