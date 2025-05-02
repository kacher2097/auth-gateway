package com.authenhub.bean;

import javax.validation.constraints.Size;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 40, message = "New password must be between 6 and 40 characters")
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
