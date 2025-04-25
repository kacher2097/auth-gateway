package com.authenhub.bean.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatar;
    private String role;
    private boolean active;
    private String socialProvider;
    private String socialId;
    private Timestamp createdAt;
    private Long roleId;
    private Timestamp updatedAt;
    private Timestamp lastLogin;
}
