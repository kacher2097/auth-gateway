package com.authenhub.bean.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequest {
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-z0-9]+:[a-z0-9]+$", message = "Name must be in format 'resource:action'")
    private String name;
    
    @NotBlank(message = "Display name is required")
    private String displayName;
    
    private String description;
    
    @NotBlank(message = "Category is required")
    private String category;
}
