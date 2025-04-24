package com.authenhub.bean.facebook.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookCommentFilterRequest {
    private String pageId;
    private String postId;
    private List<String> blockedKeywords;
    private boolean hideMatches;
    private boolean notifyOnMatch;
    private boolean isActive;
}
