package com.authenhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

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
}
