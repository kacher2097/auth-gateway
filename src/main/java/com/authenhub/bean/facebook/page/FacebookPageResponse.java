package com.authenhub.bean.facebook.page;

import com.authenhub.entity.mongo.FacebookPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPageResponse {
    private String id;
    private String pageId;
    private String pageName;
    private String category;
    private String pictureUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public static FacebookPageResponse fromEntity(FacebookPage page) {
        return FacebookPageResponse.builder()
                .id(page.getId())
                .pageId(page.getPageId())
                .pageName(page.getPageName())
                .category(page.getCategory())
                .pictureUrl(page.getPictureUrl())
                .createdAt(page.getCreatedAt())
                .updatedAt(page.getUpdatedAt())
                .build();
    }
}
