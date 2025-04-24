package com.authenhub.bean.facebook.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookTokenInfo {
    private String accessToken;
    private String tokenType;
    private Timestamp expiresAt;
    private String userId;
    private String scope;
}
