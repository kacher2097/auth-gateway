package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.webcrawler.WebCrawlerRequest;
import com.authenhub.bean.tool.webcrawler.WebCrawlerResponse;

public interface IWebCrawlerService {
    /**
     * Crawl web pages from a website
     * @param request The web crawler request
     * @return The web crawler response containing the crawled pages
     */
    WebCrawlerResponse crawlWebPages(WebCrawlerRequest request);
    
    /**
     * Get the status of a crawl job
     * @param jobId The job ID
     * @return The web crawler response containing the job status
     */
    WebCrawlerResponse getCrawlStatus(String jobId);
    
    /**
     * Export web pages to a file
     * @param format The export format (csv, json, excel)
     * @param jobId The job ID
     * @return The file content as byte array
     */
    byte[] exportWebPages(String format, String jobId);
    
    /**
     * Schedule a crawl job
     * @param request The web crawler request
     * @return The job ID
     */
    String scheduleCrawlJob(WebCrawlerRequest request);
}
