package com.authenhub.bean.facebook.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPostRequest {
    private String message;
    private String link;
    private String picture;
    private String type; // status, photo, video, link
}
