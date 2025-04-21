package com.authenhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_method_seq")
    @SequenceGenerator(name = "payment_method_seq", sequenceName = "payment_method_sequence", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "provider_type", nullable = false)
    private String providerType;

    @Column(name = "is_active")
    private boolean isActive;

    @ElementCollection
    @CollectionTable(name = "payment_method_configs", joinColumns = @JoinColumn(name = "payment_method_id"))
    @MapKeyColumn(name = "config_key")
    @Column(name = "config_value", length = 1000)
    private Map<String, String> config = new HashMap<>();

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "fee_percentage")
    private double feePercentage;

    @Column(name = "fixed_fee")
    private long fixedFee;

    @Column(name = "currency")
    private String currency;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /**
     * Convert from MongoDB entity to JPA entity
     */
    public static PaymentMethod fromMongo(com.authenhub.entity.mongo.PaymentMethod paymentMethod) {
        return PaymentMethod.builder()
                .name(paymentMethod.getName())
                .displayName(paymentMethod.getDisplayName())
                .description(paymentMethod.getDescription())
                .providerType(paymentMethod.getProviderType())
                .isActive(paymentMethod.isActive())
                .config(paymentMethod.getConfig())
                .iconUrl(paymentMethod.getIconUrl())
                .feePercentage(paymentMethod.getFeePercentage())
                .fixedFee(paymentMethod.getFixedFee())
                .currency(paymentMethod.getCurrency())
                .createdAt(paymentMethod.getCreatedAt())
                .updatedAt(paymentMethod.getUpdatedAt())
                .build();
    }

    /**
     * Convert to MongoDB entity
     */
    public com.authenhub.entity.mongo.PaymentMethod toMongo() {
        return com.authenhub.entity.mongo.PaymentMethod.builder()
                .id(this.id != null ? this.id.toString() : null)
                .name(this.name)
                .displayName(this.displayName)
                .description(this.description)
                .providerType(this.providerType)
                .isActive(this.isActive)
                .config(this.config)
                .iconUrl(this.iconUrl)
                .feePercentage(this.feePercentage)
                .fixedFee(this.fixedFee)
                .currency(this.currency)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
