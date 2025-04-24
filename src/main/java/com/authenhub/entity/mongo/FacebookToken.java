package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection = "facebook_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookToken {
    @Id
    private String id;
    
    private String userId;
    
    private String accessToken;
    
    private String tokenType;
    
    private Timestamp expiresAt;
    
    private String scope;
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
    
    // Phương thức để kiểm tra token có hết hạn không
    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()));
    }
}
