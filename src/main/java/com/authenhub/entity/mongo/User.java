package com.authenhub.entity.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String avatar;
    private Role role; // Legacy role field - will be deprecated
    private String roleId;
    private boolean active;
    private Timestamp lastLogin;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String provider;
    private String socialProvider;
    private String socialId;

    public enum Role {
        ADMIN,
        USER
    }

    // Helper method to check if user has a specific role
    public boolean hasRole(String roleId) {
        return this.roleId != null && this.roleId.contains(roleId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

            // Nếu là ADMIN, thêm tất cả các quyền cơ bản
            if (role == Role.ADMIN) {
                // Thêm các quyền cơ bản cho admin
                authorities.add(new SimpleGrantedAuthority("permission:read"));
                authorities.add(new SimpleGrantedAuthority("permission:create"));
                authorities.add(new SimpleGrantedAuthority("permission:update"));
                authorities.add(new SimpleGrantedAuthority("permission:delete"));
                authorities.add(new SimpleGrantedAuthority("role:read"));
                authorities.add(new SimpleGrantedAuthority("role:create"));
                authorities.add(new SimpleGrantedAuthority("role:update"));
                authorities.add(new SimpleGrantedAuthority("role:delete"));
                authorities.add(new SimpleGrantedAuthority("user:read"));
                authorities.add(new SimpleGrantedAuthority("user:create"));
                authorities.add(new SimpleGrantedAuthority("user:update"));
                authorities.add(new SimpleGrantedAuthority("user:delete"));
                authorities.add(new SimpleGrantedAuthority("proxy:read"));
                authorities.add(new SimpleGrantedAuthority("proxy:create"));
                authorities.add(new SimpleGrantedAuthority("proxy:update"));
                authorities.add(new SimpleGrantedAuthority("proxy:delete"));
                authorities.add(new SimpleGrantedAuthority("analytics:view"));
                authorities.add(new SimpleGrantedAuthority("analytics:export"));
                authorities.add(new SimpleGrantedAuthority("settings:read"));
                authorities.add(new SimpleGrantedAuthority("settings:update"));
            }
        }

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}