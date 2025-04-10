package com.authenhub.config;

import com.authenhub.dto.AuthResponse;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.UserRepository;
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
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

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
                    newUser.setRole(User.Role.USER);
                    newUser.setActive(true);
                    newUser.setCreatedAt(new Date());
                    newUser.setUpdatedAt(new Date());
                    return userRepository.save(newUser);
                });
        
        // Cập nhật thời gian đăng nhập
        user.setLastLogin(new Date());
        userRepository.save(user);
        
        // Tạo token JWT
        String token = jwtService.generateToken(user);
        
        // Tạo response
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserInfo.fromUser(user))
                .build();
        
        // Trả về response dưới dạng JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));
    }
} 