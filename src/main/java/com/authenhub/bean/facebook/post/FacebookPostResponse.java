package com.authenhub.bean.facebook.post;

import com.authenhub.entity.mongo.FacebookPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPostResponse {
    private String id;
    private String postId;
    private String pageId;
    private String message;
    private String link;
    private String picture;
    private String type;
    private Timestamp scheduledPublishTime;
    private boolean isPublished;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp publishedAt;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    
    public static FacebookPostResponse fromEntity(FacebookPost post) {
        return FacebookPostResponse.builder()
                .id(post.getId())
                .postId(post.getPostId())
                .pageId(post.getPageId())
                .message(post.getMessage())
                .link(post.getLink())
                .picture(post.getPicture())
                .type(post.getType())
                .scheduledPublishTime(post.getScheduledPublishTime())
                .isPublished(post.isPublished())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .sharesCount(post.getSharesCount())
                .build();
    }
}
