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
@Table(name = "api_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api_test_seq")
    @SequenceGenerator(name = "api_test_seq", sequenceName = "api_test_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String url;

    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String requestHeaders;

    private Integer timeout;

    private Boolean followRedirects;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Integer statusCode;

    private String statusText;

    @Column(columnDefinition = "TEXT")
    private String responseHeaders;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private String responseTime;

    private Long responseSize;

    private String responseContentType;

    private String error;

    private String name;

    private String description;

    private Boolean favorite;
}
