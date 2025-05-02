package com.authenhub.bean.tool.affiliatescraper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem {
    private String id;
    private String name;
    private String url;
    private String imageUrl;
    private Double originalPrice;
    private Double salePrice;
    private Double discountPercent;
    private Integer rating;
    private Integer soldCount;
    private String shopName;
    private String platform; // shopee, lazada, tiki
    private String category;
    private String affiliateUrl;
    private Double commission;
}
