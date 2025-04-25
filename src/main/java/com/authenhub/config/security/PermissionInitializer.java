package com.authenhub.config.security;

import com.authenhub.entity.Permission;
import com.authenhub.entity.Role;
import com.authenhub.entity.User;
import com.authenhub.repository.jpa.PermissionJpaRepository;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInitializer implements CommandLineRunner {

    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;
    private final PermissionJpaRepository permissionRepository;

    @Override
    public void run(String... args) {
//        initializePermissions();
//        initializeRoles();
//        updateAdminUser();
    }

    private void initializePermissions() {
        if (permissionRepository.count() > 0) {
            log.info("Permissions already initialized");
            return;
        }

        log.info("Initializing permissions...");

        List<Permission> permissions = Arrays.asList(
                // User management permissions
                createPermission("user:create", "Create User", "Permission to create new users", "User Management"),

                createPermission("user:create", "Create User", "Permission to create new users", "User Management"),
                createPermission("user:read", "View Users", "Permission to view users", "User Management"),
                createPermission("user:update", "Update User", "Permission to update users", "User Management"),
                createPermission("user:delete", "Delete User", "Permission to delete users", "User Management"),

                // Role management permissions
                createPermission("role:create", "Create Role", "Permission to create new roles", "Role Management"),
                createPermission("role:read", "View Roles", "Permission to view roles", "Role Management"),
                createPermission("role:update", "Update Role", "Permission to update roles", "Role Management"),
                createPermission("role:delete", "Delete Role", "Permission to delete roles", "Role Management"),

                // Permission management permissions
                createPermission("permission:create", "Create Permission", "Permission to create new permissions", "Permission Management"),
                createPermission("permission:read", "View Permissions", "Permission to view permissions", "Permission Management"),
                createPermission("permission:update", "Update Permission", "Permission to update permissions", "Permission Management"),
                createPermission("permission:delete", "Delete Permission", "Permission to delete permissions", "Permission Management"),

                // Proxy management permissions
                createPermission("proxy:create", "Create Proxy", "Permission to create new proxies", "Proxy Management"),
                createPermission("proxy:read", "View Proxies", "Permission to view proxies", "Proxy Management"),
                createPermission("proxy:update", "Update Proxy", "Permission to update proxies", "Proxy Management"),
                createPermission("proxy:delete", "Delete Proxy", "Permission to delete proxies", "Proxy Management"),

                // Analytics permissions
                createPermission("analytics:view", "View Analytics", "Permission to view analytics data", "Analytics"),
                createPermission("analytics:export", "Export Analytics", "Permission to export analytics data", "Analytics"),

                // Settings permissions
                createPermission("settings:read", "View Settings", "Permission to view system settings", "Settings"),
                createPermission("settings:update", "Update Settings", "Permission to update system settings", "Settings")
        );

        permissionRepository.saveAll(permissions);
        log.info("Initialized {} permissions", permissions.size());
    }

    private Permission createPermission(String name, String displayName, String description, String category) {
        return Permission.builder()
                .name(name)
                .displayName(displayName)
                .description(description)
                .category(category)
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();
    }

    private void initializeRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles already initialized");
            return;
        }

        log.info("Initializing roles...");

        // Get all permissions
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<Long> allPermissionIds = new HashSet<>();
        Set<Long> userPermissionIds = new HashSet<>();

        for (Permission permission : allPermissions) {
            allPermissionIds.add(permission.getId());

            // Add basic permissions for regular users
            if (permission.getName().equals("user:read") ||
                    permission.getName().startsWith("proxy:read") ||
                    permission.getName().equals("analytics:view")) {
                userPermissionIds.add(permission.getId());
            }
        }

        // Create admin role with all permissions
        Role adminRole = Role.builder()
                .name("admin")
                .displayName("Administrator")
                .description("Administrator role with all permissions")
                .isSystem(true)
//                .permissionIds(allPermissionIds)
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();

        // Create user role with limited permissions
        Role userRole = Role.builder()
                .name("user")
                .displayName("Regular User")
                .description("Regular user with limited permissions")
                .isSystem(true)
//                .permissionIds(userPermissionIds)
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();

        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        log.info("Initialized admin and user roles");
    }

    private void updateAdminUser() {
        Optional<User> adminUserOpt = userRepository.findByUsername("admin");
        if (adminUserOpt.isEmpty()) {
            log.info("Admin user not found, skipping role assignment");
            return;
        }

        Optional<Role> adminRoleOpt = roleRepository.findByName("admin");
        if (adminRoleOpt.isEmpty()) {
            log.info("Admin role not found, skipping role assignment");
            return;
        }

        User adminUser = adminUserOpt.get();
        Role adminRole = adminRoleOpt.get();

        // Add admin role to admin user
        adminUser.setRoleId(adminRole.getId());
        adminUser.setUpdatedAt(TimestampUtils.now());

        userRepository.save(adminUser);
        log.info("Updated admin user with admin role");
    }
}
