package com.authenhub.bean.auth;

import com.authenhub.entity.mongo.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String avatar;
    private User.Role role;

    public static UserInfo fromUser(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();
    }
}
