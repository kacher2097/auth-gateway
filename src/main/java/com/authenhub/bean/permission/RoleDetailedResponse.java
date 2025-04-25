package com.authenhub.bean.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDetailedResponse {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private boolean isSystem;
    private Set<PermissionResponse> permissions;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
