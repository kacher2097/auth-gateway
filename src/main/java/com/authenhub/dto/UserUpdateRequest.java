package com.authenhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String avatar;
}
