package com.authenhub.bean.tool.affiliatescraper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateScraperResponse {
    private List<ProductItem> products;
    private int totalCount;
    private String platform;
    private String keyword;
    private String category;
    private String scrapeDate;
    private Double totalCommission;
}
