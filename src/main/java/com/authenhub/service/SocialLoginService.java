package com.authenhub.service;

import com.authenhub.bean.auth.social.AuthSocialRequest;
import com.authenhub.bean.auth.social.SocialUserInfo;
import com.authenhub.bean.auth.social.TokenResponse;
import com.authenhub.config.fb.FacebookConfig;
import com.authenhub.config.gg.GoogleConfig;
import com.authenhub.exception.ErrorApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final GoogleConfig googleConfig;
    private final RestTemplate restTemplate;
    private final FacebookConfig facebookConfig;

    public SocialUserInfo getUserInfo(String accessToken, String provider) {
        return switch (provider.toUpperCase()) {
            case "GOOGLE" -> getGoogleUserInfo(accessToken);
            case "FACEBOOK" -> getFacebookUserInfo(accessToken);
            default -> throw new ErrorApiException("456", "Provider không hợp lệ");
        };
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
            clientId = googleConfig.getClientId();
            clientSecret = googleConfig.getClientSecret();
        } else if ("FACEBOOK".equalsIgnoreCase(provider)) {
            tokenUrl = "https://graph.facebook.com/v12.0/oauth/access_token";
            clientId = facebookConfig.getClientId();
            clientSecret = facebookConfig.getClientSecret();
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
                    googleConfig.getGoogleUserInfoUrl() + "?access_token=" + accessToken, SocialUserInfo.class
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
                    facebookConfig.getUserInfoUri() + "?fields=id,name,email,picture&access_token=" + accessToken,
                    SocialUserInfo.class
            );

            log.info("Facebook user info: {}", response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy thông tin từ Facebook: " + e.getMessage());
        }
    }
}