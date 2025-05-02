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
@Table(name = "price_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_history_seq")
    @SequenceGenerator(name = "price_history_seq", sequenceName = "price_history_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private Long trackedProductId;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Timestamp timestamp;

    private Double discountPercent;

    private Boolean isLowestPrice;

    private Boolean isHighestPrice;
}
