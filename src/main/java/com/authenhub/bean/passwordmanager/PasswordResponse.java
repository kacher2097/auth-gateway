package com.authenhub.bean.passwordmanager;

import com.authenhub.entity.PasswordManage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResponse {
    private Long id;
    private String siteUrl;
    private String username;
    private String password;
    private String iconUrl;
    private String provider;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static PasswordResponse fromEntity(PasswordManage entity) {
        return PasswordResponse.builder()
                .id(entity.getId())
                .siteUrl(entity.getSiteUrl())
                .username(entity.getUsername())
                .iconUrl(entity.getIconUrl())
                .provider(entity.getProvider())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static PasswordResponse fromEntityWithPassword(PasswordManage entity, String decryptedPassword) {
        return PasswordResponse.builder()
                .id(entity.getId())
                .siteUrl(entity.getSiteUrl())
                .username(entity.getUsername())
                .password(decryptedPassword)
                .iconUrl(entity.getIconUrl())
                .provider(entity.getProvider())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
