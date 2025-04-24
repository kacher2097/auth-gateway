package com.authenhub.controller;

import com.authenhub.bean.common.SimpleApiResponse;
import com.authenhub.bean.facebook.auth.FacebookAuthRequest;
import com.authenhub.bean.facebook.auth.FacebookAuthResponse;
import com.authenhub.bean.facebook.auth.FacebookTokenInfo;
import com.authenhub.service.interfaces.IFacebookAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facebook/auth")
@RequiredArgsConstructor
public class FacebookAuthController {
    
    private final IFacebookAuthService authService;
    
    @GetMapping("/url")
    public ResponseEntity<FacebookAuthResponse> getAuthorizationUrl(@RequestParam String redirectUri) {
        String authUrl = authService.createAuthorizationUrl(redirectUri);
        
        FacebookAuthResponse response = FacebookAuthResponse.builder()
                .authUrl(authUrl)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/callback")
    public ResponseEntity<FacebookAuthResponse> handleCallback(@RequestBody FacebookAuthRequest request) {
        FacebookTokenInfo tokenInfo = authService.handleAuthorizationCallback(request.getCode(), request.getRedirectUri());
        
        FacebookAuthResponse response = FacebookAuthResponse.builder()
                .tokenInfo(tokenInfo)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/token")
    public ResponseEntity<FacebookTokenInfo> getUserToken(@RequestParam String userId) {
        FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
        return ResponseEntity.ok(tokenInfo);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<FacebookTokenInfo> refreshToken(@RequestParam String userId) {
        FacebookTokenInfo tokenInfo = authService.refreshToken(userId);
        return ResponseEntity.ok(tokenInfo);
    }
    
    @DeleteMapping("/revoke")
    public ResponseEntity<SimpleApiResponse> revokeToken(@RequestParam String userId) {
        authService.revokeToken(userId);
        
        SimpleApiResponse response = SimpleApiResponse.builder()
                .success(true)
                .message("Token revoked successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }
}
