package com.authenhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_seq")
    @SequenceGenerator(name = "permission_seq", sequenceName = "permission_sequence", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /**
     * Utility method to create permission key
     */
    public static String createPermissionKey(String resource, String action) {
        return resource + ":" + action;
    }

    /**
     * Convert from MongoDB entity to JPA entity
     */
    public static Permission fromMongo(com.authenhub.entity.mongo.Permission permission) {
        return Permission.builder()
                .name(permission.getName())
                .displayName(permission.getDisplayName())
                .description(permission.getDescription())
                .category(permission.getCategory())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }

    /**
     * Convert to MongoDB entity
     */
    public com.authenhub.entity.mongo.Permission toMongo() {
        return com.authenhub.entity.mongo.Permission.builder()
                .id(this.id != null ? this.id.toString() : null)
                .name(this.name)
                .displayName(this.displayName)
                .description(this.description)
                .category(this.category)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
