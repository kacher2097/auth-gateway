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
@Table(name = "inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq")
    @SequenceGenerator(name = "inventory_seq", sequenceName = "inventory_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    private Integer lowStockThreshold;

    private String category;

    private String description;

    private Double costPrice;

    private Double sellingPrice;

    private String supplier;

    private String location;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String createdBy;

    private String updatedBy;
}
