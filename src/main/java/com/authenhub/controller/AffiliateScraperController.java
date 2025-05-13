package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperRequest;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperResponse;
import com.authenhub.bean.tool.affiliatescraper.ProductItem;
import com.authenhub.service.interfaces.IAffiliateScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/affiliate-scraper")
@RequiredArgsConstructor
public class AffiliateScraperController {

    private final IAffiliateScraperService affiliateScraperService;

    /**
     * Scrape products from an e-commerce platform
     * @param request The affiliate scraper request
     * @return The affiliate scraper response
     */
    @PostMapping
    public ApiResponse<AffiliateScraperResponse> scrapeProducts(@RequestBody AffiliateScraperRequest request) {
        AffiliateScraperResponse response = affiliateScraperService.scrapeProducts(request);
        return ApiResponse.success(response);
    }

    /**
     * Generate affiliate links for products
     * @param products The products to generate affiliate links for
     * @param affiliateId The affiliate ID
     * @return The products with affiliate links
     */
    @PostMapping("/generate-links")
    public ResponseEntity<ApiResponse<ProductItem[]>> generateAffiliateLinks(
            @RequestBody ProductItem[] products,
            @RequestParam String affiliateId) {
        
        ProductItem[] updatedProducts = affiliateScraperService.generateAffiliateLinks(products, affiliateId);
        return ResponseEntity.ok(ApiResponse.success(updatedProducts));
    }

    /**
     * Track product prices
     * @param productId The product ID
     * @param platform The platform
     * @param userId The user ID
     * @return True if the product is successfully added to price tracking
     */
    @PostMapping("/track-price")
    public ResponseEntity<ApiResponse<Boolean>> trackProductPrice(
            @RequestParam String productId,
            @RequestParam String platform,
            @RequestParam String userId) {
        
        boolean result = affiliateScraperService.trackProductPrice(productId, platform, userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Export products to a file
     * @param format The export format (csv, json, excel)
     * @param response The affiliate scraper response
     * @return The file content
     */
    @PostMapping("/export/{format}")
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
