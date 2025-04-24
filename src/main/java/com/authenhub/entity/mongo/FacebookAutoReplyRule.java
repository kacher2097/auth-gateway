package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection = "facebook_auto_reply_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookAutoReplyRule {
    @Id
    private String id;

    private String userId;

    private String pageId;

    private String postId;

    private String keyword;

    private String replyTemplate;

    private boolean isActive;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
