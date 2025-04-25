package com.authenhub.service;

import com.authenhub.entity.Role;
import com.authenhub.entity.User;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.interfaces.IUserRoleService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService implements IUserRoleService {

    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;

    @Override
    public User assignRolesToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate that all role IDs exist
        validateRoleIds(roleId);

        // Set the new roles
        user.setRoleId(roleId);
        user.setUpdatedAt(TimestampUtils.now());

        return userRepository.save(user);
    }

    @Override
    public User addRolesToUser(Long userId, Long roleId) {
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

    @Override
    public User removeRolesFromUser(Long userId, Long roleId) {
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

    @Override
    public Role getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<Role> roles = roleRepository.findById(user.getRoleId());
        return roles.orElse(null);
    }

    @Override
    public boolean hasPermission(User user, String permissionName) {
        if (user == null) {
            return false;
        }

        // Hỗ trợ hệ thống quyền cũ: Nếu người dùng là ADMIN, cho phép tất cả các quyền
        if (Objects.equals(user.getRole(), "ADMIN")) {
            return true;
        }

        // Nếu không có roleIds, không cần kiểm tra thêm
        if (user.getRoleId() == null) {
            return false;
        }

        Role role = roleRepository.findById(user.getRoleId()).orElse(null);
        if (role == null) {
            return false;
        }
        if (role.getName().equalsIgnoreCase("admin")) {
            return true;
        }

//        for (Long permissionId : role.getPermissionIds()) {
//            // In a real implementation, you would fetch the permission and check its name
//            // For simplicity, we'll assume the permissionId is the permission name
//            if (permissionId.equals(permissionName)) {
//                return true;
//            }
//        }
        return false;
    }

    @Override
    public void validateRoleIds(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
    }
}
