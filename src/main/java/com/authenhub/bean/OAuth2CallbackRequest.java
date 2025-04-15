package com.authenhub.bean;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuth2CallbackRequest {
    @NotBlank(message = "Authorization code is required")
    private String code;
    
    @NotBlank(message = "Provider is required")
    private String provider;
    
    @NotBlank(message = "Redirect URI is required")
    private String redirectUri;
}
