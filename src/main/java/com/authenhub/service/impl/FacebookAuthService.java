package com.authenhub.service.impl;

import com.authenhub.bean.facebook.auth.FacebookTokenInfo;
import com.authenhub.bean.facebook.auth.FacebookTokenResponse;
import com.authenhub.bean.facebook.auth.FacebookUserResponse;
import com.authenhub.config.fb.FacebookConfig;
import com.authenhub.entity.mongo.FacebookToken;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.FacebookTokenRepository;
import com.authenhub.service.client.FacebookApiClient;
import com.authenhub.service.interfaces.IFacebookAuthService;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookAuthService implements IFacebookAuthService {
    private final FacebookTokenRepository tokenRepository;
    private final FacebookApiClient apiClient;
    private final FacebookConfig facebookConfig;

    @Override
    public String createAuthorizationUrl(String redirectUri) {
        return UriComponentsBuilder
                .fromHttpUrl(facebookConfig.getAuthorizationBaseUrl())
                .queryParam("client_id", facebookConfig.getClientId())
                .queryParam("redirect_uri", facebookConfig.getRedirectUri())
                .queryParam("scope", facebookConfig.getScope())
                .queryParam("response_type", "code")
                .build()
                .toUriString();
    }

    @Override
    public FacebookTokenInfo handleAuthorizationCallback(String code, String redirectUri) {
        // Gửi request đến Facebook để đổi code lấy token
        Map<String, String> params = new HashMap<>();
        params.put("client_id", facebookConfig.getClientId());
        params.put("client_secret", facebookConfig.getClientSecret());
        params.put("code", code);
        params.put("redirect_uri", facebookConfig.getRedirectUri());
        params.put("grant_type", "authorization_code");

        FacebookTokenResponse tokenResponse = apiClient.get("/oauth/access_token", null, FacebookTokenResponse.class, params);

        // Lấy thông tin người dùng từ token
        Map<String, String> userParams = new HashMap<>();
        userParams.put("fields", "id,name,email");

        FacebookUserResponse userResponse = apiClient.get("/me", tokenResponse.getAccessToken(), FacebookUserResponse.class, userParams);

        // Kiểm tra xem người dùng đã có token chưa
        tokenRepository.findByUserId(userResponse.getId())
                .ifPresent(tokenRepository::delete);

        // Lưu token vào database
        FacebookToken token = FacebookToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(userResponse.getId())
                .accessToken(tokenResponse.getAccessToken())
                .tokenType("bearer")
                .expiresAt(calculateExpiryTime(tokenResponse.getExpiresIn()))
                .scope(tokenResponse.getScope())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        tokenRepository.save(token);

        // Trả về thông tin token
        return FacebookTokenInfo.builder()
                .accessToken(token.getAccessToken())
                .tokenType(token.getTokenType())
                .expiresAt(token.getExpiresAt())
                .userId(token.getUserId())
                .scope(token.getScope())
                .build();
    }

    @Override
    public FacebookTokenInfo getUserToken(String userId) {
        FacebookToken token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found for user: " + userId));

        if (token.isExpired()) {
            return refreshToken(userId);
        }

        return FacebookTokenInfo.builder()
                .accessToken(token.getAccessToken())
                .tokenType(token.getTokenType())
                .expiresAt(token.getExpiresAt())
                .userId(token.getUserId())
                .scope(token.getScope())
                .build();
    }

    @Override
    public FacebookTokenInfo refreshToken(String userId) {
        FacebookToken oldToken = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found for user: " + userId));

        // Gửi request đến Facebook để làm mới token
        Map<String, String> params = new HashMap<>();
        params.put("client_id", facebookConfig.getClientId());
        params.put("client_secret", facebookConfig.getClientSecret());
        params.put("grant_type", "fb_exchange_token");
        params.put("fb_exchange_token", oldToken.getAccessToken());

        FacebookTokenResponse tokenResponse = apiClient.get("/oauth/access_token", null, FacebookTokenResponse.class, params);

        // Cập nhật token trong database
        oldToken.setAccessToken(tokenResponse.getAccessToken());
        oldToken.setExpiresAt(calculateExpiryTime(tokenResponse.getExpiresIn()));
        oldToken.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        tokenRepository.save(oldToken);

        // Trả về thông tin token mới
        return FacebookTokenInfo.builder()
                .accessToken(oldToken.getAccessToken())
                .tokenType(oldToken.getTokenType())
                .expiresAt(oldToken.getExpiresAt())
                .userId(oldToken.getUserId())
                .scope(oldToken.getScope())
                .build();
    }

    @Override
    public void revokeToken(String userId) {
        FacebookToken token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found for user: " + userId));

        // Gửi request đến Facebook để hủy token
        apiClient.delete("/me/permissions", token.getAccessToken());

        // Xóa token khỏi database
        tokenRepository.delete(token);
    }

    private Timestamp calculateExpiryTime(long expiresInSeconds) {
        long expiryTimeMillis = System.currentTimeMillis() + (expiresInSeconds * 1000);
        return new Timestamp(expiryTimeMillis);
    }
}
