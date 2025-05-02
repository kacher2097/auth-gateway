package com.authenhub.entity;

import com.authenhub.utils.TimestampUtils;
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

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_token_seq")
    @SequenceGenerator(name = "password_reset_token_seq", sequenceName = "password_reset_token_sequence", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expiry_date", nullable = false)
    private Timestamp expiryDate;

    @Column(name = "used")
    private boolean used;

    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return TimestampUtils.now().after(expiryDate);
    }

    /**
     * Convert from MongoDB entity to JPA entity
     */
    public static PasswordResetToken fromMongo(com.authenhub.entity.mongo.PasswordResetToken token) {
        return PasswordResetToken.builder()
                .token(token.getToken())
                .userId(token.getUserId())
                .expiryDate(token.getExpiryDate())
                .used(token.isUsed())
                .build();
    }

    /**
     * Convert to MongoDB entity
     */
    public com.authenhub.entity.mongo.PasswordResetToken toMongo() {
        com.authenhub.entity.mongo.PasswordResetToken token = new com.authenhub.entity.mongo.PasswordResetToken();
        token.setId(this.id != null ? this.id.toString() : null);
        token.setToken(this.token);
        token.setUserId(this.userId);
        token.setExpiryDate(this.expiryDate);
        token.setUsed(this.used);
        return token;
    }
}
