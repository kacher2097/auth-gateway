package com.authenhub.bean.facebook.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookScheduledPostRequest {
    private String message;
    private String link;
    private String picture;
    private String type; // status, photo, video, link
    private Timestamp scheduledPublishTime;
}
