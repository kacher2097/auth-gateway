package com.authenhub.dto;

import com.authenhub.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.Set;

public class RoleDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "Name is required")
        private String name;
        
        @NotBlank(message = "Display name is required")
        private String displayName;
        
        private String description;
        
        @NotEmpty(message = "At least one permission is required")
        private Set<String> permissionIds;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String name;
        private String displayName;
        private String description;
        private boolean isSystem;
        private Set<String> permissionIds;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        
        public static Response fromEntity(Role role) {
            return Response.builder()
                    .id(role.getId())
                    .name(role.getName())
                    .displayName(role.getDisplayName())
                    .description(role.getDescription())
                    .isSystem(role.isSystem())
                    .permissionIds(role.getPermissionIds())
                    .createdAt(role.getCreatedAt())
                    .updatedAt(role.getUpdatedAt())
                    .build();
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailedResponse {
        private String id;
        private String name;
        private String displayName;
        private String description;
        private boolean isSystem;
        private Set<PermissionDto.Response> permissions;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }
}
