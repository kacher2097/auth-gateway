package com.authenhub.service.impl;

import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperRequest;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperResponse;
import com.authenhub.bean.tool.affiliatescraper.ProductItem;
import com.authenhub.service.interfaces.IAffiliateScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AffiliateScraperService implements IAffiliateScraperService {

    // In-memory storage for price tracking (would be replaced with a database in production)
    private final Map<String, List<String>> priceTracking = new HashMap<>();

    @Override
    public AffiliateScraperResponse scrapeProducts(AffiliateScraperRequest request) {
        log.info("Scraping products from platform: {} with keyword: {}", request.getPlatform(), request.getKeyword());
        
        // TODO: Implement actual scraping logic for different platforms
        
        // For now, return mock data
        List<ProductItem> products = new ArrayList<>();
        int limit = request.getLimit() != null ? request.getLimit() : 10;
        
        for (int i = 1; i <= limit; i++) {
            double originalPrice = 100.0 * i;
            double discountPercent = i * 5.0;
            double salePrice = originalPrice * (1 - discountPercent / 100);
            
            products.add(ProductItem.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Product " + i + " - " + request.getKeyword())
                    .url(getPlatformBaseUrl(request.getPlatform()) + "/product-" + i)
                    .imageUrl(getPlatformBaseUrl(request.getPlatform()) + "/images/product-" + i + ".jpg")
                    .originalPrice(originalPrice)
                    .salePrice(salePrice)
                    .discountPercent(discountPercent)
                    .rating(i % 5 + 1)
                    .soldCount(i * 100)
                    .shopName("Shop " + i)
                    .platform(request.getPlatform())
                    .category(request.getCategory() != null ? request.getCategory() : "Electronics")
                    .commission(salePrice * 0.05) // 5% commission
                    .build());
        }
        
        // Filter by price range if specified
        if (request.getMinPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getSalePrice() >= request.getMinPrice())
                    .collect(Collectors.toList());
        }
        
        if (request.getMaxPrice() != null) {
            products = products.stream()
                    .filter(p -> p.getSalePrice() <= request.getMaxPrice())
                    .collect(Collectors.toList());
        }
        
        // Filter by rating if specified
        if (request.getMinRating() != null) {
            products = products.stream()
                    .filter(p -> p.getRating() >= request.getMinRating())
                    .collect(Collectors.toList());
        }
        
        // Filter by sold count if specified
        if (request.getMinSoldCount() != null) {
            products = products.stream()
                    .filter(p -> p.getSoldCount() >= request.getMinSoldCount())
                    .collect(Collectors.toList());
        }
        
        // Sort products if specified
        if (request.getSortBy() != null) {
            switch (request.getSortBy()) {
                case "sales":
                    products.sort((p1, p2) -> p2.getSoldCount().compareTo(p1.getSoldCount()));
                    break;
                case "price_asc":
                    products.sort((p1, p2) -> p1.getSalePrice().compareTo(p2.getSalePrice()));
                    break;
                case "price_desc":
                    products.sort((p1, p2) -> p2.getSalePrice().compareTo(p1.getSalePrice()));
                    break;
                case "rating":
                    products.sort((p1, p2) -> p2.getRating().compareTo(p1.getRating()));
                    break;
                default:
                    // Default sort by relevance (no sorting)
                    break;
            }
        }
        
        // Generate affiliate links if affiliate ID is provided
        if (request.getAffiliateId() != null && !request.getAffiliateId().isEmpty()) {
//            products = generateAffiliateLinks(products.toArray(new ProductItem[0]), request.getAffiliateId());
        }
        
        // Calculate total commission
        double totalCommission = products.stream()
                .mapToDouble(ProductItem::getCommission)
                .sum();
        
        return AffiliateScraperResponse.builder()
                .products(products)
                .totalCount(products.size())
                .platform(request.getPlatform())
                .keyword(request.getKeyword())
                .category(request.getCategory())
                .scrapeDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .totalCommission(totalCommission)
                .build();
    }

    @Override
    public ProductItem[] generateAffiliateLinks(ProductItem[] products, String affiliateId) {
        log.info("Generating affiliate links for {} products with affiliate ID: {}", products.length, affiliateId);
        
        for (ProductItem product : products) {
            String baseUrl = product.getUrl();
            String affiliateUrl = baseUrl + "?aff=" + affiliateId;
            product.setAffiliateUrl(affiliateUrl);
        }
        
        return products;
    }

    @Override
    public boolean trackProductPrice(String productId, String platform, String userId) {
        log.info("Tracking price for product ID: {} on platform: {} for user: {}", productId, platform, userId);
        
        String key = userId + ":" + platform;
        if (!priceTracking.containsKey(key)) {
            priceTracking.put(key, new ArrayList<>());
        }
        
        priceTracking.get(key).add(productId);
        
        return true;
    }

    @Override
    public byte[] exportProducts(String format, AffiliateScraperResponse affiliateScraperResponse) {
        log.info("Exporting products to format: {}", format);
        
        // TODO: Implement actual export logic based on the format
        
        // For now, return mock data
        StringBuilder sb = new StringBuilder();
        
        if ("json".equalsIgnoreCase(format)) {
            sb.append("{\n");
            sb.append("  \"products\": [\n");
            for (int i = 0; i < affiliateScraperResponse.getProducts().size(); i++) {
                ProductItem product = affiliateScraperResponse.getProducts().get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(product.getId()).append("\",\n");
                sb.append("      \"name\": \"").append(product.getName()).append("\",\n");
                sb.append("      \"url\": \"").append(product.getUrl()).append("\",\n");
                sb.append("      \"originalPrice\": ").append(product.getOriginalPrice()).append(",\n");
                sb.append("      \"salePrice\": ").append(product.getSalePrice()).append(",\n");
                sb.append("      \"discountPercent\": ").append(product.getDiscountPercent()).append(",\n");
                sb.append("      \"platform\": \"").append(product.getPlatform()).append("\",\n");
                if (product.getAffiliateUrl() != null) {
                    sb.append("      \"affiliateUrl\": \"").append(product.getAffiliateUrl()).append("\",\n");
                }
                sb.append("      \"commission\": ").append(product.getCommission()).append("\n");
                sb.append("    }");
                if (i < affiliateScraperResponse.getProducts().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ],\n");
            sb.append("  \"totalCount\": ").append(affiliateScraperResponse.getTotalCount()).append(",\n");
            sb.append("  \"platform\": \"").append(affiliateScraperResponse.getPlatform()).append("\",\n");
            sb.append("  \"keyword\": \"").append(affiliateScraperResponse.getKeyword()).append("\",\n");
            sb.append("  \"scrapeDate\": \"").append(affiliateScraperResponse.getScrapeDate()).append("\",\n");
            sb.append("  \"totalCommission\": ").append(affiliateScraperResponse.getTotalCommission()).append("\n");
            sb.append("}");
        } else if ("csv".equalsIgnoreCase(format)) {
            sb.append("id,name,url,originalPrice,salePrice,discountPercent,platform,affiliateUrl,commission\n");
            for (ProductItem product : affiliateScraperResponse.getProducts()) {
                sb.append(product.getId()).append(",");
                sb.append("\"").append(product.getName()).append("\",");
                sb.append("\"").append(product.getUrl()).append("\",");
                sb.append(product.getOriginalPrice()).append(",");
                sb.append(product.getSalePrice()).append(",");
                sb.append(product.getDiscountPercent()).append(",");
                sb.append("\"").append(product.getPlatform()).append("\",");
                sb.append("\"").append(product.getAffiliateUrl() != null ? product.getAffiliateUrl() : "").append("\",");
                sb.append(product.getCommission()).append("\n");
            }
        } else {
            // Default to plain text
            sb.append("Products from ").append(affiliateScraperResponse.getPlatform()).append("\n");
            sb.append("Keyword: ").append(affiliateScraperResponse.getKeyword()).append("\n");
            sb.append("Scraped on: ").append(affiliateScraperResponse.getScrapeDate()).append("\n");
            sb.append("Total Commission: ").append(affiliateScraperResponse.getTotalCommission()).append("\n\n");
            
            for (ProductItem product : affiliateScraperResponse.getProducts()) {
                sb.append("Name: ").append(product.getName()).append("\n");
                sb.append("URL: ").append(product.getUrl()).append("\n");
                sb.append("Original Price: ").append(product.getOriginalPrice()).append("\n");
                sb.append("Sale Price: ").append(product.getSalePrice()).append("\n");
                sb.append("Discount: ").append(product.getDiscountPercent()).append("%\n");
                if (product.getAffiliateUrl() != null) {
                    sb.append("Affiliate URL: ").append(product.getAffiliateUrl()).append("\n");
                }
                sb.append("Commission: ").append(product.getCommission()).append("\n\n");
            }
        }
        
        return sb.toString().getBytes();
    }
    
    private String getPlatformBaseUrl(String platform) {
        if (platform == null) {
            return "https://example.com";
        }
        
        switch (platform.toLowerCase()) {
            case "shopee":
                return "https://shopee.vn";
            case "lazada":
                return "https://lazada.vn";
            case "tiki":
                return "https://tiki.vn";
            default:
                return "https://example.com";
        }
    }
}
