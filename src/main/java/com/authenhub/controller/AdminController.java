package com.authenhub.controller;

import com.authenhub.bean.UserUpdateRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.user.UserSearchResponse;
import com.authenhub.entity.User;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "API quản trị hệ thống: quản lý người dùng, phân quyền, thống kê")
public class AdminController {

    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;
    private final UserManagementService userManagementService;

    @GetMapping("/users")
    @Operation(summary = "Lấy danh sách tất cả người dùng",
            description = "Trả về danh sách tất cả người dùng trong hệ thống.")
    public com.authenhub.bean.common.ApiResponse<?> getAllUsers() {
        return getUsersList(null, null, null);
    }

    @PostMapping("/users")
    @Operation(summary = "Lấy danh sách người dùng với bộ lọc",
            description = "Trả về danh sách người dùng với các tùy chọn lọc, phân trang và tìm kiếm.")
    public com.authenhub.bean.common.ApiResponse<?> getUsersPost(@RequestBody(required = false) Map<String, Object> params) {
        Integer page = params != null && params.containsKey("page") ? Integer.valueOf(params.get("page").toString()) : null;
        Integer limit = params != null && params.containsKey("limit") ? Integer.valueOf(params.get("limit").toString()) : null;
        String search = params != null && params.containsKey("search") ? params.get("search").toString() : null;

        return getUsersList(page, limit, search);
    }

    private com.authenhub.bean.common.ApiResponse<?> getUsersList(Integer page, Integer limit, String search) {
        List<User> users = userRepository.findAll();
        List<UserSearchResponse> response = new ArrayList<>();
        for (User user : users) {
            UserSearchResponse userSearchResponse = UserSearchResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .avatar(user.getAvatar())
                    .roleId(user.getRoleId())
                    .active(user.isActive())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .lastLogin(user.getLastLogin())
                    .build();
            roleRepository.findById(user.getRoleId()).ifPresent(role -> userSearchResponse.setRole(role.getDisplayName()));
            response.add(userSearchResponse);
        }
        log.info("Get all users successfully total {} users", response.size());
        return com.authenhub.bean.common.ApiResponse.success(response);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Lấy thông tin người dùng theo ID",
            description = "Trả về thông tin chi tiết của người dùng dựa trên ID.")
    public ApiResponse<?> getUserById(@PathVariable Long id) {
        return getUserByIdInternal(id);
    }

    @PostMapping("/users/{id}")
    @Operation(summary = "Lấy thông tin người dùng theo ID (POST)",
            description = "Trả về thông tin chi tiết của người dùng dựa trên ID sử dụng phương thức POST.")
    public ApiResponse<?> getUserByIdPost(@PathVariable Long id) {
        return getUserByIdInternal(id);
    }

    private ApiResponse<?> getUserByIdInternal(Long id) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ApiResponse.error("404", "User not found");
        }

        User user = userOpt.get();
        user.setPassword(null); // Remove sensitive information

        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Cập nhật thông tin người dùng",
            description = "Cập nhật thông tin của người dùng dựa trên ID.")
    public ApiResponse<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        User updatedUser = userManagementService.updateUser(id, request);
        updatedUser.setPassword(null); // Remove sensitive information

        return ApiResponse.success(updatedUser);
    }

    @PutMapping("/users/{id}/activate")
    @Operation(summary = "Kích hoạt tài khoản người dùng",
            description = "Kích hoạt tài khoản của người dùng dựa trên ID.")
    public ApiResponse<?> activateUser(@PathVariable Long id) {
        User user = userManagementService.setUserActiveStatus(id, true);
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa tài khoản người dùng",
            description = "Vô hiệu hóa tài khoản của người dùng dựa trên ID.")
    public ApiResponse<?> deactivateUser(@PathVariable Long id) {
        User user = userManagementService.setUserActiveStatus(id, false);
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/promote-to-admin")
    @Operation(summary = "Nâng cấp người dùng lên quyền admin",
            description = "Nâng cấp quyền của người dùng lên admin dựa trên ID.")
    public ApiResponse<?> promoteToAdmin(@PathVariable Long id) {
        User user = userManagementService.setUserRole(id, "ADMIN");
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}/demote-to-user")
    @Operation(summary = "Hạ cấp người dùng xuống quyền user",
            description = "Hạ cấp quyền của người dùng xuống user dựa trên ID.")
    public ApiResponse<?> demoteToUser(@PathVariable Long id) {
        User user = userManagementService.setUserRole(id, "USER");
        return ApiResponse.success(user);
    }
}
