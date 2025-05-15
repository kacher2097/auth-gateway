package com.authenhub.bean.auth.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSocialRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("redirect_uri")
    private String redirectUri;
    private String code;
    private String state;

    @JsonProperty("grant_type")
    private String grantType;
}
