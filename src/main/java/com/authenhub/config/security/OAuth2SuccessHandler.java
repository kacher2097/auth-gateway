package com.authenhub.config.security;

import com.authenhub.bean.auth.AuthResponse;
import com.authenhub.bean.auth.UserInfo;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.utils.TimestampUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserJpaRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

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

        // Trả về response dưới dạng JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));
    }
}