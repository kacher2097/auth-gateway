package com.authenhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "tracked_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tracked_product_seq")
    @SequenceGenerator(name = "tracked_product_seq", sequenceName = "tracked_product_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String platform; // shopee, lazada, tiki

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    private String imageUrl;

    @Column(nullable = false)
    private Double originalPrice;

    @Column(nullable = false)
    private Double currentPrice;

    private Double lowestPrice;

    private Double highestPrice;

    private Timestamp lowestPriceDate;

    private Timestamp highestPriceDate;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp lastCheckedAt;

    private String affiliateUrl;

    private Double commission;

    private Boolean notifyOnPriceChange;

    private Double targetPrice;

    private Boolean notifyOnTargetPrice;
}
