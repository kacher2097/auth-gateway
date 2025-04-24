package com.authenhub.bean.facebook.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookAuthResponse {
    private String authUrl;
    private FacebookTokenInfo tokenInfo;
}
