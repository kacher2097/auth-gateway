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
@Table(name = "web_crawler_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebCrawlerJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "web_crawler_job_seq")
    @SequenceGenerator(name = "web_crawler_job_seq", sequenceName = "web_crawler_job_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String url;

    private Integer depth;

    private Integer maxPages;

    private String includePattern;

    private String excludePattern;

    private String userAgent;

    private Integer timeout;

    @Column(columnDefinition = "TEXT")
    private String contentTypes;

    private String cssSelector;

    private String xpath;

    private Boolean respectRobotsTxt;

    private String scheduleType; // once, daily, weekly

    private String scheduleTime;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp completedAt;

    private Timestamp nextRunAt;

    @Column(nullable = false)
    private String status; // PENDING, RUNNING, COMPLETED, FAILED, SCHEDULED

    private String errorMessage;

    private Integer totalPages;

    private String crawlDuration;

    @Column(columnDefinition = "TEXT")
    private String resultJson;
}
