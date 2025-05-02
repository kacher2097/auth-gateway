package com.authenhub.bean.tool.affiliatescraper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateScraperRequest {
    private String userId;
    private String platform; // shopee, lazada, tiki
    private String keyword;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private Integer minRating;
    private Integer minSoldCount;
    private String sortBy; // relevance, sales, price_asc, price_desc, rating
    private Integer limit;
    private String affiliateId;
}
