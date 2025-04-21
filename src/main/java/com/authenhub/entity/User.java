package com.authenhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private com.authenhub.entity.mongo.User.Role role; // Legacy role field - will be deprecated

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "active")
    private boolean active;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "provider")
    private String provider;

    @Column(name = "social_provider")
    private String socialProvider;

    @Column(name = "social_id")
    private String socialId;

    // Helper method to check if user has a specific role
    public boolean hasRole(String roleId) {
        return this.roleId != null && this.roleId.contains(roleId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

            // If ADMIN, add all basic permissions
            if (role == com.authenhub.entity.mongo.User.Role.ADMIN) {
                // Add basic permissions for admin
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

    /**
     * Convert from MongoDB entity to JPA entity
     */
    public static User fromMongo(com.authenhub.entity.mongo.User user) {
        User userJpa = User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .roleId(user.getRoleId())
                .active(user.isActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .provider(user.getProvider())
                .socialProvider(user.getSocialProvider())
                .socialId(user.getSocialId())
                .build();

        // ID will be auto-generated by PostgreSQL sequence
        return userJpa;
    }

    /**
     * Convert to MongoDB entity
     */
    public com.authenhub.entity.mongo.User toMongo() {
        com.authenhub.entity.mongo.User user = new com.authenhub.entity.mongo.User();
        user.setId(this.id != null ? this.id.toString() : null);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setFullName(this.fullName);
        user.setAvatar(this.avatar);
        user.setRole(this.role);
        user.setRoleId(this.roleId);
        user.setActive(this.active);
        user.setLastLogin(this.lastLogin);
        user.setCreatedAt(this.createdAt);
        user.setUpdatedAt(this.updatedAt);
        user.setProvider(this.provider);
        user.setSocialProvider(this.socialProvider);
        user.setSocialId(this.socialId);
        return user;
    }
}
