package com.authenhub.bean.facebook.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPostListResponse {
    private List<FacebookPostResponse> posts;
    private int totalCount;
    private int page;
    private int size;
    private boolean hasNext;
}
