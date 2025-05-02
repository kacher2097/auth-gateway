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
import java.util.Objects;
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

    @Column(name = "role")
    private String role; // Legacy role field - will be deprecated

    @Column(name = "role_id")
    private Long roleId;

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

//    private String phoneNumber;
//    private String address;
//    private Boolean emailVerified;
//    private Boolean phoneVerified;
//    private Boolean enabled;
//    private Boolean locked;
//    private String verificationToken;
//    private Timestamp verificationTokenExpiry;
//    private String resetPasswordToken;
//    private Timestamp resetPasswordTokenExpiry;
//    private Integer toolCredits;
//    private Timestamp subscriptionStartDate;
//    private Timestamp subscriptionEndDate;
//    private String paymentMethod;
//    private String stripeCustomerId;
//    private String referralCode;
//    private String referredBy;
//    private Integer referralCount;

    // Helper method to check if user has a specific role
    public boolean hasRole(Long roleId) {
        return this.roleId != null && this.roleId.equals(roleId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

            // If ADMIN, add all basic permissions
            if (Objects.equals(role, "ADMIN")) {
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
}
