package com.authenhub.service.impl;

import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperRequest;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperResponse;
import com.authenhub.bean.tool.affiliatescraper.ProductItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AffiliateScraperServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AffiliateScraperService affiliateScraperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScrapeProducts_WithShopee_ShouldReturnMockData() {
        // Given
        AffiliateScraperRequest request = AffiliateScraperRequest.builder()
                .platform("shopee")
                .keyword("smartphone")
                .limit(5)
                .build();

        // Mock the RestTemplate to return an error so it falls back to mock data
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Connection error"));

        // When
        AffiliateScraperResponse response = affiliateScraperService.scrapeProducts(request);

        // Then
        assertNotNull(response);
        assertEquals("shopee", response.getPlatform());
        assertEquals("smartphone", response.getKeyword());
        assertNotNull(response.getProducts());
        assertEquals(5, response.getProducts().size());
        assertEquals(5, response.getTotalCount());
        
        // Verify product details
        ProductItem firstProduct = response.getProducts().get(0);
        assertEquals("shopee", firstProduct.getPlatform());
        assertTrue(firstProduct.getName().contains("smartphone"));
        assertNotNull(firstProduct.getUrl());
        assertNotNull(firstProduct.getImageUrl());
        assertNotNull(firstProduct.getOriginalPrice());
        assertNotNull(firstProduct.getSalePrice());
        assertNotNull(firstProduct.getDiscountPercent());
    }

    @Test
    void testScrapeProducts_WithOtherPlatform_ShouldReturnMockData() {
        // Given
        AffiliateScraperRequest request = AffiliateScraperRequest.builder()
                .platform("lazada")
                .keyword("laptop")
                .limit(3)
                .build();

        // When
        AffiliateScraperResponse response = affiliateScraperService.scrapeProducts(request);

        // Then
        assertNotNull(response);
        assertEquals("lazada", response.getPlatform());
        assertEquals("laptop", response.getKeyword());
        assertNotNull(response.getProducts());
        assertEquals(3, response.getProducts().size());
        assertEquals(3, response.getTotalCount());
        
        // Verify product details
        ProductItem firstProduct = response.getProducts().get(0);
        assertEquals("lazada", firstProduct.getPlatform());
        assertTrue(firstProduct.getName().contains("laptop"));
    }

    @Test
    void testGenerateAffiliateLinks() {
        // Given
        ProductItem product1 = ProductItem.builder()
                .id("1")
                .name("Test Product 1")
                .url("https://example.com/product-1")
                .build();
        
        ProductItem product2 = ProductItem.builder()
                .id("2")
                .name("Test Product 2")
                .url("https://example.com/product-2")
                .build();
        
        ProductItem[] products = new ProductItem[] { product1, product2 };
        String affiliateId = "aff123";

        // When
        ProductItem[] result = affiliateScraperService.generateAffiliateLinks(products, affiliateId);

        // Then
        assertEquals(2, result.length);
        assertEquals("https://example.com/product-1?aff=aff123", result[0].getAffiliateUrl());
        assertEquals("https://example.com/product-2?aff=aff123", result[1].getAffiliateUrl());
    }

    @Test
    void testTrackProductPrice() {
        // Given
        String productId = "prod123";
        String platform = "shopee";
        String userId = "user123";

        // When
        boolean result = affiliateScraperService.trackProductPrice(productId, platform, userId);

        // Then
        assertTrue(result);
    }

    @Test
    void testExportProducts_Json() {
        // Given
        ProductItem product = ProductItem.builder()
                .id("1")
                .name("Test Product")
                .url("https://example.com/product-1")
                .originalPrice(100.0)
                .salePrice(80.0)
                .discountPercent(20.0)
                .platform("shopee")
                .commission(4.0)
                .build();
        
        AffiliateScraperResponse response = AffiliateScraperResponse.builder()
                .products(List.of(product))
                .totalCount(1)
                .platform("shopee")
                .keyword("test")
                .scrapeDate("2023-05-15T10:00:00")
                .totalCommission(4.0)
                .build();

        // When
        byte[] result = affiliateScraperService.exportProducts("json", response);

        // Then
        assertNotNull(result);
        String jsonContent = new String(result);
        assertTrue(jsonContent.contains("\"products\""));
        assertTrue(jsonContent.contains("\"id\": \"1\""));
        assertTrue(jsonContent.contains("\"name\": \"Test Product\""));
    }

    @Test
    void testExportProducts_Csv() {
        // Given
        ProductItem product = ProductItem.builder()
                .id("1")
                .name("Test Product")
                .url("https://example.com/product-1")
                .originalPrice(100.0)
                .salePrice(80.0)
                .discountPercent(20.0)
                .platform("shopee")
                .commission(4.0)
                .build();
        
        AffiliateScraperResponse response = AffiliateScraperResponse.builder()
                .products(List.of(product))
                .totalCount(1)
                .platform("shopee")
                .keyword("test")
                .scrapeDate("2023-05-15T10:00:00")
                .totalCommission(4.0)
                .build();

        // When
        byte[] result = affiliateScraperService.exportProducts("csv", response);

        // Then
        assertNotNull(result);
        String csvContent = new String(result);
        assertTrue(csvContent.contains("id,name,url,originalPrice,salePrice,discountPercent,platform,affiliateUrl,commission"));
        assertTrue(csvContent.contains("1,\"Test Product\",\"https://example.com/product-1\",100.0,80.0,20.0,\"shopee\",\"\",4.0"));
    }
}
