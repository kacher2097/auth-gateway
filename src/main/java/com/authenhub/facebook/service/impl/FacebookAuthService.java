//package com.authenhub.facebook.service.impl;
//
//import com.authenhub.exception.ResourceNotFoundException;
//import com.authenhub.facebook.bean.auth.FacebookTokenInfo;
//import com.authenhub.facebook.bean.auth.FacebookTokenResponse;
//import com.authenhub.facebook.bean.auth.FacebookUserResponse;
//import com.authenhub.facebook.config.FacebookOAuthConfig;
//import com.authenhub.facebook.entity.FacebookToken;
//import com.authenhub.facebook.repository.FacebookTokenRepository;
//import com.authenhub.facebook.service.client.FacebookApiClient;
//import com.authenhub.facebook.service.interfaces.IFacebookAuthService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.sql.Timestamp;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class FacebookAuthService implements IFacebookAuthService {
//    private final FacebookTokenRepository tokenRepository;
//    private final FacebookApiClient apiClient;
//    private final FacebookOAuthConfig oauthConfig;
//
//    @Override
//    public String createAuthorizationUrl(String redirectUri) {
//        return UriComponentsBuilder
//            .fromHttpUrl(oauthConfig.getAuthorizationBaseUrl())
//            .queryParam("client_id", oauthConfig.getClientId())
//            .queryParam("redirect_uri", redirectUri)
//            .queryParam("scope", oauthConfig.getDefaultScope())
//            .queryParam("response_type", "code")
//            .build()
//            .toUriString();
//    }
//
//    @Override
//    public FacebookTokenInfo handleAuthorizationCallback(String code, String redirectUri) {
//        // Gửi request đến Facebook để đổi code lấy token
//        Map<String, String> params = new HashMap<>();
//        params.put("client_id", oauthConfig.getClientId());
//        params.put("client_secret", oauthConfig.getClientSecret());
//        params.put("code", code);
//        params.put("redirect_uri", redirectUri);
//        params.put("grant_type", "authorization_code");
//
//        log.info("Exchanging code for token with params: {}", params);
//        try {
//            FacebookTokenResponse tokenResponse = apiClient.get("/oauth/access_token", null, FacebookTokenResponse.class, params);
//            log.info("Successfully obtained token response: {}", tokenResponse);
//
//            // Lấy thông tin người dùng từ token
//            Map<String, String> userParams = new HashMap<>();
//            userParams.put("fields", "id,name,email");
//
//            FacebookUserResponse userResponse = apiClient.get("/me", tokenResponse.getAccessToken(), FacebookUserResponse.class, userParams);
//
//            // Kiểm tra xem người dùng đã có token chưa
//            tokenRepository.findByUserId(userResponse.getId())
//                .ifPresent(tokenRepository::delete);
//
//            // Lưu token vào database
//            FacebookToken token = FacebookToken.builder()
//                .id(UUID.randomUUID().toString())
//                .userId(userResponse.getId())
//                .accessToken(tokenResponse.getAccessToken())
//                .tokenType("bearer")
//                .expiresAt(calculateExpiryTime(tokenResponse.getExpiresIn()))
//                .scope(tokenResponse.getScope())
//                .createdAt(new Timestamp(System.currentTimeMillis()))
//                .updatedAt(new Timestamp(System.currentTimeMillis()))
//                .build();
//
//            tokenRepository.save(token);
//
//            // Trả về thông tin token
//            return FacebookTokenInfo.builder()
//                .accessToken(token.getAccessToken())
//                .tokenType(token.getTokenType())
//                .expiresAt(token.getExpiresAt())
//                .userId(token.getUserId())
//                .scope(token.getScope())
//                .build();
//        } catch (RestClientException e) {
//            log.error("Error exchanging code for token: {}", e.getMessage(), e);
//            throw new RuntimeException("Error exchanging code for token: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public FacebookTokenInfo getUserToken(String userId) {
//        FacebookToken token = tokenRepository.findByUserId(userId)
//            .orElseThrow(() -> new ResourceNotFoundException("Token not found for user: " + userId));
//
//        if (token.isExpired()) {
//            return refreshToken(userId);
//        }
//
//        return FacebookTokenInfo.builder()
//            .accessToken(token.getAccessToken())
//            .tokenType(token.getTokenType())
//            .expiresAt(token.getExpiresAt())
//            .userId(token.getUserId())
//            .scope(token.getScope())
//            .build();
//    }
//
//    @Override
//    public FacebookTokenInfo refreshToken(String userId) {
//        FacebookToken oldToken = tokenRepository.findByUserId(userId)
//            .orElseThrow(() -> new ResourceNotFoundException("Token not found for user: " + userId));
//
//        // Gửi request đến Facebook để làm mới token
//        Map<String, String> params = new HashMap<>();
//        params.put("client_id", oauthConfig.getClientId());
//        params.put("client_secret", oauthConfig.getClientSecret());
//        params.put("grant_type", "fb_exchange_token");
//        params.put("fb_exchange_token", oldToken.getAccessToken());
//
//        try {
//            FacebookTokenResponse tokenResponse = apiClient.get("/oauth/access_token", null, FacebookTokenResponse.class, params);
//
//            // Cập nhật token trong database
//            oldToken.setAccessToken(tokenResponse.getAccessToken());
//            oldToken.setExpiresAt(calculateExpiryTime(tokenResponse.getExpiresIn()));
//            oldToken.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//
//            tokenRepository.save(oldToken);
//
//            // Trả về thông tin token mới
//            return FacebookTokenInfo.builder()
//                .accessToken(oldToken.getAccessToken())
//                .tokenType(oldToken.getTokenType())
//                .expiresAt(oldToken.getExpiresAt())
//                .userId(oldToken.getUserId())
//                .scope(oldToken.getScope())
//                .build();
//        } catch (RestClientException e) {
//            log.error("Error refreshing token: {}", e.getMessage(), e);
//            throw new RuntimeException("Error refreshing token: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void revokeToken(String userId) {
//        FacebookToken token = tokenRepository.findByUserId(userId)
//            .orElseThrow(() -> new ResourceNotFoundException("Token not found for user: " + userId));
//
//        try {
//            // Gửi request đến Facebook để hủy token
//            apiClient.delete("/me/permissions", token.getAccessToken());
//
//            // Xóa token khỏi database
//            tokenRepository.delete(token);
//        } catch (RestClientException e) {
//            log.error("Error revoking token: {}", e.getMessage(), e);
//            throw new RuntimeException("Error revoking token: " + e.getMessage(), e);
//        }
//    }
//
//    private Timestamp calculateExpiryTime(long expiresInSeconds) {
//        long expiryTimeMillis = System.currentTimeMillis() + (expiresInSeconds * 1000);
//        return new Timestamp(expiryTimeMillis);
//    }
//}
