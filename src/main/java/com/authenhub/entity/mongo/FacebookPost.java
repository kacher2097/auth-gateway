package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection = "facebook_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPost {
    @Id
    private String id;
    
    private String userId;
    
    private String pageId;
    
    private String postId;
    
    private String message;
    
    private String link;
    
    private String picture;
    
    private String type; // status, photo, video, link
    
    private Timestamp scheduledPublishTime;
    
    private boolean isPublished;
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
    
    private Timestamp publishedAt;
    
    private int likesCount;
    
    private int commentsCount;
    
    private int sharesCount;
}
