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
@Table(name = "sales_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_history_seq")
    @SequenceGenerator(name = "sales_history_seq", sequenceName = "sales_history_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Timestamp saleDate;

    private Double salePrice;

    private String channel;

    private String orderId;

    private String customerId;

    @Column(nullable = false)
    private Timestamp createdAt;

    private String createdBy;
}
