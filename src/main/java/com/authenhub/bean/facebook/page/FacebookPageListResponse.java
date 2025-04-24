package com.authenhub.bean.facebook.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPageListResponse {
    private List<FacebookPageResponse> pages;
    private int totalCount;
}
