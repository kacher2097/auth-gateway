package com.authenhub.bean.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String displayName;
    private String description;
    
    @NotEmpty(message = "At least one permission is required")
    private Set<String> permissions;
}
