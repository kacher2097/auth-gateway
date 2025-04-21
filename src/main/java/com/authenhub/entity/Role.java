package com.authenhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_sequence", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_system")
    private boolean isSystem;

    @ElementCollection
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission_id")
    private Set<String> permissionIds = new HashSet<>();

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /**
     * Convert from MongoDB entity to JPA entity
     */
    public static Role fromMongo(com.authenhub.entity.mongo.Role role) {
        return Role.builder()
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .isSystem(role.isSystem())
                .permissionIds(role.getPermissionIds())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }

    /**
     * Convert to MongoDB entity
     */
    public com.authenhub.entity.mongo.Role toMongo() {
        return com.authenhub.entity.mongo.Role.builder()
                .id(this.id != null ? this.id.toString() : null)
                .name(this.name)
                .displayName(this.displayName)
                .description(this.description)
                .isSystem(this.isSystem)
                .permissionIds(this.permissionIds)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
