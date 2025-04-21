package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.entity.mongo.Role;
import com.authenhub.entity.mongo.User;
import com.authenhub.repository.RoleRepository;
import com.authenhub.repository.UserRepository;
import com.authenhub.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth/admin/migrate")
@RequiredArgsConstructor
public class AdminMigrationController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleService userRoleService;

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse> migrateAdminRoles() {
        // Tìm người dùng admin
        Optional<User> adminUserOpt = userRepository.findByUsername("admin");
        if (adminUserOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(false)
                    .message("Admin user not found")
                    .build());
        }

        // Tìm vai trò admin
        Optional<Role> adminRoleOpt = roleRepository.findByName("admin");
        if (adminRoleOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(false)
                    .message("Admin role not found")
                    .build());
        }

        User adminUser = adminUserOpt.get();
        Role adminRole = adminRoleOpt.get();
        User updatedUser = userRoleService.assignRolesToUser(adminUser.getId(), adminRole.getId());
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Admin roles migrated successfully")
                .data(updatedUser)
                .build());
    }
}
