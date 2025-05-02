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
@Table(name = "code_generations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "code_generation_seq")
    @SequenceGenerator(name = "code_generation_seq", sequenceName = "code_generation_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String codeType; // crud, component, api

    private String modelName;

    private String framework;

    private String database;

    @Column(columnDefinition = "TEXT")
    private String fields;

    private String componentName;

    private String componentType; // functional, class

    @Column(columnDefinition = "TEXT")
    private String features; // Comma-separated list of features

    private String apiName;

    private Boolean authRequired;

    private String language; // javascript, typescript, java, python, php

    @Column(nullable = false)
    private Timestamp createdAt;

    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String generatedCode;

    private String name;

    private String description;

    private Boolean favorite;
}
