package com.authenhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "forecast_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forecast_data_seq")
    @SequenceGenerator(name = "forecast_data_seq", sequenceName = "forecast_data_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Timestamp forecastDate;

    @Column(nullable = false)
    private Integer predictedQuantity;

    private Double confidenceLevel;

    private String forecastModel; // The algorithm or model used for forecasting

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String createdBy;

    private String updatedBy;
}
