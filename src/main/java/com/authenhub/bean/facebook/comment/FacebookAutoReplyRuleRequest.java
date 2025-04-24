package com.authenhub.bean.facebook.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookAutoReplyRuleRequest {
    private String pageId;
    private String postId;
    private String keyword;
    private String replyTemplate;
    private boolean isActive;
}
