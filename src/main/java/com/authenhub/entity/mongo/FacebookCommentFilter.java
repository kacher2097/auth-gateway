package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.List;

@Document(collection = "facebook_comment_filters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookCommentFilter {
    @Id
    private String id;
    
    private String userId;
    
    private String pageId;
    
    private String postId;
    
    private List<String> blockedKeywords;
    
    private boolean hideMatches;
    
    private boolean notifyOnMatch;
    
    private boolean isActive;
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
}
