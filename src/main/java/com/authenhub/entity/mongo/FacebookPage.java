package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection = "facebook_pages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPage {
    @Id
    private String id;
    
    private String userId;
    
    private String pageId;
    
    private String pageName;
    
    private String pageToken;
    
    private String category;
    
    private String pictureUrl;
    
    private Timestamp createdAt;
    
    private Timestamp updatedAt;
}
