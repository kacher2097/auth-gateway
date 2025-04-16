package com.authenhub.service;

import com.authenhub.entity.Role;
import com.authenhub.entity.User;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.RoleRepository;
import com.authenhub.repository.UserRepository;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User assignRolesToUser(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate that all role IDs exist
        validateRoleIds(roleId);

        // Set the new roles
        user.setRoleId(roleId);
        user.setUpdatedAt(TimestampUtils.now());

        return userRepository.save(user);
    }

    public User addRolesToUser(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate that all role IDs exist
        validateRoleIds(roleId);

        // Add new roles
//        Set<String> updatedRoles = new HashSet<>(user.getRoleIds() != null ? user.getRoleIds() : new HashSet<>());
//        updatedRoles.addAll(roleIds);
//        user.setRoleIds(updatedRoles);
//        user.setUpdatedAt(TimestampUtils.now());

        return userRepository.save(user);
    }

    public User removeRolesFromUser(String userId, String roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Remove roles
        if (user.getRoleId() != null) {
//            Set<String> updatedRoles = new HashSet<>(user.getRoleIds());
//            updatedRoles.removeAll(roleIds);
            user.setRoleId(roleId);
            user.setUpdatedAt(TimestampUtils.now());
        }

        return userRepository.save(user);
    }

    public Role getUserRoles(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<Role> roles = roleRepository.findById(user.getRoleId());
        return roles.orElse(null);
    }

    public boolean hasPermission(User user, String permissionName) {
        if (user == null) {
            return false;
        }

        // Hỗ trợ hệ thống quyền cũ: Nếu người dùng là ADMIN, cho phép tất cả các quyền
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }

        // Nếu không có roleIds, không cần kiểm tra thêm
        if (user.getRoleId() == null || user.getRoleId().isEmpty()) {
            return false;
        }

        Role role = roleRepository.findById(user.getRoleId()).orElse(null);
        if (role.getName().equalsIgnoreCase("admin")) {
            return true;
        }

        for (String permissionId : role.getPermissionIds()) {
            // In a real implementation, you would fetch the permission and check its name
            // For simplicity, we'll assume the permissionId is the permission name
            if (permissionId.equals(permissionName)) {
                return true;
            }
        }
        return false;
    }

    private void validateRoleIds(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
    }
}
