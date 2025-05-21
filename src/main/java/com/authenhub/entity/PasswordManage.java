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

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_manage")
public class PasswordManage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_manage_seq")
    @SequenceGenerator(name = "password_manage_seq", sequenceName = "password_manage_sequence", allocationSize = 1)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "site_url", nullable = false)
    private String siteUrl;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "encrypted_password", length = 512)
    private String encryptedPassword;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "provider")
    private String provider;
}
