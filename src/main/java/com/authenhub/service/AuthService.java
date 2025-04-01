package com.authenhub.service;

import com.authenhub.dto.AuthRequest;
import com.authenhub.dto.AuthResponse;
import com.authenhub.dto.RegisterRequest;
import com.authenhub.dto.SocialLoginRequest;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SocialLoginService socialLoginService;

    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra username và email đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Tạo user mới
        var user = new com.authenhub.entity.User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(com.authenhub.entity.User.Role.USER);
        user.setActive(true);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        userRepository.save(user);

        // Tạo token và response
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserInfo.fromUser(user))
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        // Xác thực thông tin đăng nhập
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Lấy thông tin user
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Cập nhật thời gian đăng nhập
        user.setLastLogin(new Date());
        userRepository.save(user);

        // Tạo token và response
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserInfo.fromUser(user))
                .build();
    }

    public AuthResponse socialLogin(SocialLoginRequest request) {
        // Lấy thông tin user từ social provider
        var socialUserInfo = socialLoginService.getUserInfo(request.getAccessToken(), request.getProvider());

        // Tìm user theo email
        var user = userRepository.findByEmail(socialUserInfo.getEmail())
                .orElseGet(() -> {
                    // Tạo user mới nếu chưa tồn tại
                    var newUser = new User();
                    newUser.setEmail(socialUserInfo.getEmail());
                    newUser.setUsername(socialUserInfo.getEmail()); // Hoặc có thể tạo username từ email
                    newUser.setFullName(socialUserInfo.getName());
                    newUser.setAvatar(socialUserInfo.getPicture());
                    newUser.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                    newUser.setRole(User.Role.USER);
                    newUser.setActive(true);
                    newUser.setCreatedAt(new Date());
                    newUser.setUpdatedAt(new Date());
                    return userRepository.save(newUser);
                });

        // Cập nhật thời gian đăng nhập
        user.setLastLogin(new Date());
        userRepository.save(user);

        // Tạo token và response
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserInfo.fromUser(user))
                .build();
    }
} 