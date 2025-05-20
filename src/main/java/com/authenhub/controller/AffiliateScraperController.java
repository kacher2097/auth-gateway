package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperRequest;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperResponse;
import com.authenhub.bean.tool.affiliatescraper.ProductItem;
import com.authenhub.service.interfaces.IAffiliateScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/affiliate-scraper")
@RequiredArgsConstructor
@Tag(name = "Affiliate Scraper", description = "API để thu thập và quản lý sản phẩm từ các nền tảng thương mại điện tử")
public class AffiliateScraperController {

    private final IAffiliateScraperService affiliateScraperService;

    @PostMapping
    @Operation(summary = "Thu thập sản phẩm từ nền tảng thương mại điện tử",
            description = "Thu thập thông tin sản phẩm từ các nền tảng thương mại điện tử như Shopee, Lazada, Tiki, v.v.")
    public ApiResponse<AffiliateScraperResponse> scrapeProducts(@RequestBody AffiliateScraperRequest request) {
        AffiliateScraperResponse response = affiliateScraperService.scrapeProducts(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/generate-links")
    @Operation(summary = "Tạo liên kết tiếp thị liên kết cho sản phẩm",
            description = "Tạo liên kết tiếp thị liên kết cho các sản phẩm đã thu thập để kiếm hoa hồng.")
    public ApiResponse<?> generateAffiliateLinks(
            @RequestBody ProductItem[] products,
            @RequestParam String affiliateId) {

        ProductItem[] updatedProducts = affiliateScraperService.generateAffiliateLinks(products, affiliateId);
        return ApiResponse.success(updatedProducts);
    }

    @PostMapping("/track-price")
    @Operation(summary = "Theo dõi giá sản phẩm",
            description = "Thêm sản phẩm vào danh sách theo dõi giá để nhận thông báo khi giá thay đổi.")
    public ApiResponse<?> trackProductPrice(
            @RequestParam String productId,
            @RequestParam String platform,
            @RequestParam String userId) {

        boolean result = affiliateScraperService.trackProductPrice(productId, platform, userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/export/{format}")
    @Operation(summary = "Xuất sản phẩm ra file",
            description = "Xuất danh sách sản phẩm đã thu thập ra file với các định dạng khác nhau (CSV, JSON, Excel).")
    public ResponseEntity<byte[]> exportProducts(
            @PathVariable String format,
            @RequestBody AffiliateScraperResponse response) {

        byte[] fileContent = affiliateScraperService.exportProducts(format, response);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "products." + format);

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
