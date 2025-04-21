package com.authenhub.dto;

import com.authenhub.entity.mongo.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.sql.Timestamp;

public class PermissionDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "Name is required")
        @Pattern(regexp = "^[a-z0-9]+:[a-z0-9]+$", message = "Name must be in format 'resource:action'")
        private String name;
        
        @NotBlank(message = "Display name is required")
        private String displayName;
        
        private String description;
        
        @NotBlank(message = "Category is required")
        private String category;
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
        private String category;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        
        public static Response fromEntity(Permission permission) {
            return Response.builder()
                    .id(permission.getId())
                    .name(permission.getName())
                    .displayName(permission.getDisplayName())
                    .description(permission.getDescription())
                    .category(permission.getCategory())
                    .createdAt(permission.getCreatedAt())
                    .updatedAt(permission.getUpdatedAt())
                    .build();
        }
    }
}
