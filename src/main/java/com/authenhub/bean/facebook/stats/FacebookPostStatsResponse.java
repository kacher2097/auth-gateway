package com.authenhub.bean.facebook.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPostStatsResponse {
    private String postId;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    private int reach;
    private int engagement;
    private int clicks;
    private Timestamp timestamp;
}
