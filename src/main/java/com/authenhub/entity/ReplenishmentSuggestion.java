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
@Table(name = "replenishment_suggestions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplenishmentSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "replenishment_suggestion_seq")
    @SequenceGenerator(name = "replenishment_suggestion_seq", sequenceName = "replenishment_suggestion_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Integer suggestedQuantity;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, MODIFIED

    private Integer approvedQuantity;

    private String approvedBy;

    private Timestamp approvedAt;

    @Column(nullable = false)
    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String createdBy;

    private String updatedBy;
}
