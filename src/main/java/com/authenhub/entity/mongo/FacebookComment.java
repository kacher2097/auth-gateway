package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection = "facebook_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookComment {
    @Id
    private String id;
    
    private String userId;
    
    private String postId;
    
    private String commentId;
    
    private String message;
    
    private String fromId;
    
    private String fromName;
    
    private boolean isHidden;
    
    private boolean isAutoReplied;
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
}
