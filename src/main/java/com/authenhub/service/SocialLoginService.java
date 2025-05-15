package com.authenhub.service;

import com.authenhub.bean.auth.social.AuthSocialRequest;
import com.authenhub.bean.auth.social.SocialUserInfo;
import com.authenhub.bean.auth.social.TokenResponse;
import com.authenhub.exception.ErrorApiException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.user-info-uri:https://www.googleapis.com/oauth2/v3/userinfo}")
    private String googleUserInfoUrl;

    @Value("${spring.security.oauth2.client.registration.facebook.user-info-uri:https://graph.facebook.com/me}")
    private String facebookUserInfoUrl;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String facebookClientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String facebookClientSecret;

    public SocialUserInfo getUserInfo(String accessToken, String provider) {
        switch (provider.toUpperCase()) {
            case "GOOGLE":
                return getGoogleUserInfo(accessToken);
            case "FACEBOOK":
                return getFacebookUserInfo(accessToken);
            default:
                throw new ErrorApiException("456", "Provider không hợp lệ");
        }
    }

    public SocialUserInfo exchangeCodeForUserInfo(String code, String provider, String redirectUri) {
        // Exchange authorization code for access token
        String accessToken = getAccessTokenFromCode(code, provider, redirectUri);

        // Get user info using the access token
        return getUserInfo(accessToken, provider);
    }

    private String getAccessTokenFromCode(String code, String provider, String redirectUri) {
        final String tokenUrl;
        final String clientId;
        final String clientSecret;

        if ("GOOGLE".equalsIgnoreCase(provider)) {
            tokenUrl = "https://oauth2.googleapis.com/token";
            clientId = googleClientId;
            clientSecret = googleClientSecret;
        } else if ("FACEBOOK".equalsIgnoreCase(provider)) {
            tokenUrl = "https://graph.facebook.com/v12.0/oauth/access_token";
            clientId = facebookClientId;
            clientSecret = facebookClientSecret;
        } else {
            throw new RuntimeException("Provider không hợp lệ");
        }

        AuthSocialRequest authSocialRequest = AuthSocialRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .provider(provider)
                .redirectUri(redirectUri)
                .grantType("authorization_code")
                .build();
        // Make POST request to token endpoint
        try {
            // This is a simplified example - in a real app, you'd use proper OAuth2 libraries
            // or Spring Security OAuth2 support
            TokenResponse response = restTemplate.postForObject(
                tokenUrl,
                    authSocialRequest,
                TokenResponse.class
            );

            if (response != null && response.getAccessToken() != null) {
                return response.getAccessToken();
            } else {
                throw new RuntimeException("Failed to get access token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error exchanging code for token: " + e.getMessage(), e);
        }
    }

    private SocialUserInfo getGoogleUserInfo(String accessToken) {
        try {
            var response = restTemplate.getForObject(
                    googleUserInfoUrl + "?access_token=" + accessToken, SocialUserInfo.class
            );
            log.info("Google user info: {}", response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy thông tin từ Google: " + e.getMessage());
        }
    }

    private SocialUserInfo getFacebookUserInfo(String accessToken) {
        try {
            var response = restTemplate.getForObject(
                    facebookUserInfoUrl + "?fields=id,name,email,picture&access_token=" + accessToken,
                    SocialUserInfo.class
            );

            log.info("Facebook user info: {}", response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy thông tin từ Facebook: " + e.getMessage());
        }
    }
}