package com.authenhub.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import com.authenhub.utils.TimestampUtils;

@Data
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    private String id;
    private String token;
    private String userId;
    private Timestamp expiryDate;
    private boolean used;

    public boolean isExpired() {
        return TimestampUtils.now().after(expiryDate);
    }
}
