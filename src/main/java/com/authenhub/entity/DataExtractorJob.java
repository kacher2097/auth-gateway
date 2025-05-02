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
@Table(name = "data_extractor_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataExtractorJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_extractor_job_seq")
    @SequenceGenerator(name = "data_extractor_job_seq", sequenceName = "data_extractor_job_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String extractionType; // web, file, text, database

    private String url;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String fileContent;

    private String fileType;

    private String databaseType;

    private String databaseUrl;

    private String databaseUsername;

    @Column(columnDefinition = "TEXT")
    private String dataTypes; // Comma-separated list of data types

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp completedAt;

    @Column(nullable = false)
    private String status; // PENDING, RUNNING, COMPLETED, FAILED

    private String errorMessage;

    private Integer totalItems;

    @Column(columnDefinition = "TEXT")
    private String typeCounts; // JSON string with counts by type

    private String extractionTime;

    @Column(columnDefinition = "TEXT")
    private String resultJson;
}
