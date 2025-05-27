package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.crawl.DataCrawlRequest;
import com.authenhub.service.crawler.HandleCrawlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vnexpress")
@Tag(name = "VnExpress Crawler", description = "API để thu thập và xử lý dữ liệu từ VnExpress")
public class VnexpressController {

    private final HandleCrawlService vnExpressCrawler;

    public VnexpressController(@Qualifier("vnexpress") HandleCrawlService vnExpressCrawler) {
        this.vnExpressCrawler = vnExpressCrawler;
    }

    @PostMapping("/v2/latest")
    @Operation(summary = "Lấy tin tức mới nhất",
            description = "Lấy danh sách các tin tức mới nhất từ VnExpress.")
    public ApiResponse<?> getLatestNewsV2(@RequestBody DataCrawlRequest request) throws Exception {
        return ApiResponse.success(vnExpressCrawler.getData(request));
    }

    @PostMapping("/v1/export")
    @Operation(summary = "Xuất tin tức ra file Excel",
            description = "Xuất danh sách các tin tức đã thu thập ra file Excel.")
    public ApiResponse<?> exportNews(@RequestBody DataCrawlRequest wpPublishPostBean) throws IOException {
        try {
            // Wait for the CompletableFuture to complete with a timeout of 5 minutes
            Object result = vnExpressCrawler.exportExcel(wpPublishPostBean).get(5, TimeUnit.MINUTES);
            return ApiResponse.success(result);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw new IOException("Failed to export Excel: " + cause.getMessage(), cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Export operation was interrupted", e);
        } catch (TimeoutException e) {
            throw new IOException("Export operation timed out after 5 minutes", e);
        }
    }

    @PostMapping("/v1/publish")
    @Operation(summary = "Đăng tin tức lên WordPress",
            description = "Đăng các tin tức đã thu thập lên trang WordPress.")
    public ResponseEntity<String> publishPost(@RequestBody DataCrawlRequest wpPublishPostBean) throws Exception {
        vnExpressCrawler.importToWp(wpPublishPostBean);
        return null;
    }
}
