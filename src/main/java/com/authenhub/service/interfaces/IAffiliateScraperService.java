package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperRequest;
import com.authenhub.bean.tool.affiliatescraper.AffiliateScraperResponse;
import com.authenhub.bean.tool.affiliatescraper.ProductItem;

public interface IAffiliateScraperService {
    /**
     * Scrape products from an e-commerce platform
     * @param request The affiliate scraper request
     * @return The affiliate scraper response containing the scraped products
     */
    AffiliateScraperResponse scrapeProducts(AffiliateScraperRequest request);
    
    /**
     * Generate affiliate links for products
     * @param products The products to generate affiliate links for
     * @param affiliateId The affiliate ID
     * @return The products with affiliate links
     */
    ProductItem[] generateAffiliateLinks(ProductItem[] products, String affiliateId);
    
    /**
     * Track product prices
     * @param productId The product ID
     * @param platform The platform
     * @param userId The user ID
     * @return True if the product is successfully added to price tracking
     */
    boolean trackProductPrice(String productId, String platform, String userId);
    
    /**
     * Export products to a file
     * @param format The export format (csv, json, excel)
     * @param affiliateScraperResponse The affiliate scraper response to export
     * @return The file content as byte array
     */
    byte[] exportProducts(String format, AffiliateScraperResponse affiliateScraperResponse);
}
