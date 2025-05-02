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
@Table(name = "affiliate_scraper_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateScraperJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "affiliate_scraper_job_seq")
    @SequenceGenerator(name = "affiliate_scraper_job_seq", sequenceName = "affiliate_scraper_job_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
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

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp completedAt;

    @Column(nullable = false)
    private String status; // PENDING, RUNNING, COMPLETED, FAILED

    private String errorMessage;

    private Integer totalProducts;

    private Double totalCommission;

    @Column(columnDefinition = "TEXT")
    private String resultJson;
}
