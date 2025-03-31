package com.authenhub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLoginRequest {
    @NotBlank(message = "Access token không được để trống")
    private String accessToken;
    
    @NotBlank(message = "Provider không được để trống")
    private String provider; // GOOGLE, FACEBOOK
} 