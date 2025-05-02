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
@Table(name = "blog_crawler_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCrawlerJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blog_crawler_job_seq")
    @SequenceGenerator(name = "blog_crawler_job_seq", sequenceName = "blog_crawler_job_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String url;

    private String category;

    private String searchKeyword;

    private String dateFrom;

    private String dateTo;

    private String author;

    private String sortBy;

    private String sortOrder;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp completedAt;

    @Column(nullable = false)
    private String status; // PENDING, RUNNING, COMPLETED, FAILED

    private String errorMessage;

    private Integer totalPosts;

    @Column(columnDefinition = "TEXT")
    private String resultJson;
}
