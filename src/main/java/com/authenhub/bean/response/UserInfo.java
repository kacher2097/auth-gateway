package com.authenhub.bean.response;

import com.authenhub.dto.AuthResponse;
import com.authenhub.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String avatar;
    private User.Role role;

    public static AuthResponse.UserInfo fromUser(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();
    }
}
