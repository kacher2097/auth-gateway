package com.authenhub.bean.facebook.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPageRequest {
    private String name;
    private String description;
    private String pictureUrl;
}
