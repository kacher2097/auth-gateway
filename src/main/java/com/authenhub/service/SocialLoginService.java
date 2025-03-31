package com.authenhub.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${social.google.user-info-url}")
    private String googleUserInfoUrl;

    @Value("${social.facebook.user-info-url}")
    private String facebookUserInfoUrl;

    public SocialUserInfo getUserInfo(String accessToken, String provider) {
        switch (provider.toUpperCase()) {
            case "GOOGLE":
                return getGoogleUserInfo(accessToken);
            case "FACEBOOK":
                return getFacebookUserInfo(accessToken);
            default:
                throw new RuntimeException("Provider không hợp lệ");
        }
    }

    private SocialUserInfo getGoogleUserInfo(String accessToken) {
        try {
            var response = restTemplate.getForObject(
                    googleUserInfoUrl + "?access_token=" + accessToken,
                    GoogleUserInfo.class
            );
            return new SocialUserInfo(
                    response.getEmail(),
                    response.getName(),
                    response.getPicture()
            );
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy thông tin từ Google: " + e.getMessage());
        }
    }

    private SocialUserInfo getFacebookUserInfo(String accessToken) {
        try {
            var response = restTemplate.getForObject(
                    facebookUserInfoUrl + "?fields=id,name,email,picture&access_token=" + accessToken,
                    FacebookUserInfo.class
            );
            return new SocialUserInfo(
                    response.getEmail(),
                    response.getName(),
                    response.getPicture().getData().url
            );
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy thông tin từ Facebook: " + e.getMessage());
        }
    }

    @Data
    public static class SocialUserInfo {
        private final String email;
        private final String name;
        private final String picture;
    }

    @Data
    private static class GoogleUserInfo {
        private String email;
        private String name;
        private String picture;
    }

    @Data
    private static class FacebookUserInfo {
        private String email;
        private String name;
        private Picture picture;

        @Data
        private static class Picture {
            private Data data;

            @lombok.Data
            private static class Data {
                private String url;
            }
        }
    }
} 