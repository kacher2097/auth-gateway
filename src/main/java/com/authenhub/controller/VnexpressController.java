package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.crawl.DataCrawlRequest;
import com.authenhub.service.crawler.HandleCrawlService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/vnexpress")
public class VnexpressController {

    private final HandleCrawlService vnExpressCrawler;

    public VnexpressController(@Qualifier("vnexpress") HandleCrawlService vnExpressCrawler) {
        this.vnExpressCrawler = vnExpressCrawler;
    }

    @PostMapping("/v2/latest")
    public ApiResponse<?> getLatestNewsV2(@RequestBody DataCrawlRequest request) throws Exception {
        return ApiResponse.success(vnExpressCrawler.getData(request));
    }

    @PostMapping("/v1/export")
    public CompletableFuture<Object> exportNews(@RequestBody DataCrawlRequest wpPublishPostBean) throws IOException {
        return vnExpressCrawler.exportExcel(wpPublishPostBean);
    }

    @PostMapping("/v1/publish")
    public ResponseEntity<String> publishPost(@RequestBody DataCrawlRequest wpPublishPostBean) throws Exception {
        vnExpressCrawler.importToWp(wpPublishPostBean);
        return null;
    }
}
