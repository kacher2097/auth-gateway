package com.authenhub.controller;

import com.authenhub.bean.auth.AuthResponse;
import com.authenhub.bean.auth.UserInfo;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final JwtService jwtService;
    private final UserJpaRepository userRepository;

    @GetMapping("/callback")
    public ApiResponse<?> oauth2Callback(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        // Tìm user theo email
        var user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Tạo user mới nếu chưa tồn tại
                    var newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email); // Hoặc có thể tạo username từ email
                    newUser.setFullName(name);
                    newUser.setAvatar(picture);
                    newUser.setPassword("OAUTH2_USER"); // Không cần mật khẩu cho OAuth2
                    newUser.setRole("USER");
                    newUser.setActive(true);
                    newUser.setCreatedAt(TimestampUtils.now());
                    newUser.setUpdatedAt(TimestampUtils.now());
                    return userRepository.save(newUser);
                });

        // Cập nhật thời gian đăng nhập
        user.setLastLogin(TimestampUtils.now());
        userRepository.save(user);

        // Tạo token JWT
        String token = jwtService.generateToken(user);

        // Tạo response
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(UserInfo.fromUser(user))
                .build();

        return ApiResponse.success(authResponse);
    }
}