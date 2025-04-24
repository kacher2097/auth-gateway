package com.authenhub.bean.facebook.comment;

import com.authenhub.entity.mongo.FacebookComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookCommentResponse {
    private String id;
    private String commentId;
    private String postId;
    private String message;
    private String fromId;
    private String fromName;
    private boolean isHidden;
    private boolean isAutoReplied;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public static FacebookCommentResponse fromEntity(FacebookComment comment) {
        return FacebookCommentResponse.builder()
                .id(comment.getId())
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .message(comment.getMessage())
                .fromId(comment.getFromId())
                .fromName(comment.getFromName())
                .isHidden(comment.isHidden())
                .isAutoReplied(comment.isAutoReplied())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
