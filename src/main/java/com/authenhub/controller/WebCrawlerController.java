package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.webcrawler.WebCrawlerRequest;
import com.authenhub.bean.tool.webcrawler.WebCrawlerResponse;
import com.authenhub.service.interfaces.IWebCrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/web-crawler")
@RequiredArgsConstructor
@Tag(name = "Web Crawler", description = "API để thu thập dữ liệu từ các trang web")
public class WebCrawlerController {

    private final IWebCrawlerService webCrawlerService;

    @PostMapping
    @Operation(summary = "Thu thập dữ liệu từ trang web",
            description = "Thu thập dữ liệu từ một trang web dựa trên các tham số đầu vào.")
    public ApiResponse<?> crawlWebPages(@RequestBody WebCrawlerRequest request) {
        WebCrawlerResponse response = webCrawlerService.crawlWebPages(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Lấy trạng thái của công việc thu thập",
            description = "Lấy trạng thái và kết quả của một công việc thu thập dữ liệu dựa trên ID.")
    public ApiResponse<?> getCrawlStatus(@PathVariable String jobId) {
        WebCrawlerResponse response = webCrawlerService.getCrawlStatus(jobId);
        return ApiResponse.success(response);
    }

    @GetMapping("/export/{format}/{jobId}")
    @Operation(summary = "Xuất dữ liệu thu thập ra file",
            description = "Xuất dữ liệu đã thu thập ra file với các định dạng khác nhau (CSV, JSON, Excel).")
    public ResponseEntity<byte[]> exportWebPages(
            @PathVariable String format,
            @PathVariable String jobId) {

        byte[] fileContent = webCrawlerService.exportWebPages(format, jobId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "web-pages." + format);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @PostMapping("/schedule")
    @Operation(summary = "Lên lịch công việc thu thập dữ liệu",
            description = "Lên lịch một công việc thu thập dữ liệu để chạy trong tương lai.")
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
