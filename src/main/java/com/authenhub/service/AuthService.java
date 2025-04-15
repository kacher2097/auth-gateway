package com.authenhub.service;

import com.authenhub.dto.*;
import com.authenhub.entity.PasswordResetToken;
import com.authenhub.exception.*;
import com.authenhub.repository.PasswordResetTokenRepository;
import com.authenhub.service.SocialLoginService.SocialUserInfo;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import com.authenhub.utils.TimestampUtils;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SocialLoginService socialLoginService;
    private final EmailService emailService;

    // Optional dependency that will only be injected in dev profile
    private final MockEmailService mockEmailService;

    public AuthService(UserRepository userRepository,
                      PasswordResetTokenRepository tokenRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager,
                      SocialLoginService socialLoginService,
                      EmailService emailService,
                      @org.springframework.beans.factory.annotation.Autowired(required = false) MockEmailService mockEmailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.socialLoginService = socialLoginService;
        this.emailService = emailService;
        this.mockEmailService = mockEmailService;
    }

    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra username và email đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        // Tạo user mới
        var user = new com.authenhub.entity.User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(com.authenhub.entity.User.Role.USER);
        user.setActive(true);
        user.setCreatedAt(TimestampUtils.now());
        user.setUpdatedAt(TimestampUtils.now());

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
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Lấy thông tin user
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Cập nhật thời gian đăng nhập
        user.setLastLogin(TimestampUtils.now());
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
                    newUser.setCreatedAt(TimestampUtils.now());
                    newUser.setUpdatedAt(TimestampUtils.now());
                    newUser.setSocialProvider(request.getProvider());
//                    newUser.setSocialId(socialUserInfo.getId());
                    return userRepository.save(newUser);
                });

        // Cập nhật thời gian đăng nhập
        user.setLastLogin(TimestampUtils.now());
        userRepository.save(user);

        // Tạo token và response
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserInfo.fromUser(user))
                .build();
    }

    public AuthResponse.UserInfo getCurrentUser(String token) {
        // Lấy username từ token
        String jwt = token.substring(7); // Bỏ "Bearer " ở đầu
        String username = jwtService.extractUsername(jwt);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        return AuthResponse.UserInfo.fromUser(user);
    }

    public AuthResponse handleOAuth2Callback(OAuth2CallbackRequest request) {
        try {
            // Exchange authorization code for user info
            SocialUserInfo userInfo = socialLoginService.exchangeCodeForUserInfo(
                    request.getCode(),
                    request.getProvider(),
                    request.getRedirectUri()
            );

            // Find or create user
            User user = userRepository.findByEmail(userInfo.getEmail())
                    .orElseGet(() -> {
                        // Create new user if not exists
                        User newUser = new User();
                        newUser.setEmail(userInfo.getEmail());
                        newUser.setUsername(userInfo.getEmail()); // Use email as username
                        newUser.setFullName(userInfo.getName());
                        newUser.setAvatar(userInfo.getPicture());
                        newUser.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                        newUser.setRole(User.Role.USER);
                        newUser.setActive(true);
                        newUser.setCreatedAt(TimestampUtils.now());
                        newUser.setUpdatedAt(TimestampUtils.now());
                        newUser.setSocialProvider(request.getProvider());
                        return userRepository.save(newUser);
                    });

            // Update last login time
            user.setLastLogin(TimestampUtils.now());
            userRepository.save(user);

            // Generate JWT token
            String token = jwtService.generateToken(user);

            // Return response
            return AuthResponse.builder()
                    .token(token)
                    .user(AuthResponse.UserInfo.fromUser(user))
                    .build();
        } catch (Exception e) {
            log.error("OAuth2 callback error", e);
            throw new RuntimeException("OAuth2 authentication failed: " + e.getMessage());
        }
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(TimestampUtils.now());
        userRepository.save(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(EmailNotFoundException::new);

        // Delete any existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());

        // Generate token
        String token = generateResetToken();

        // Save token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUserId(user.getId());
        resetToken.setExpiryDate(TimestampUtils.addHours(TimestampUtils.now(), 24)); // Token valid for 24 hours
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        // Send email (this won't throw exceptions even if email sending fails)
        try {
            // Try to use the real email service first
            try {
                emailService.sendPasswordResetEmail(user.getEmail(), token);
            } catch (Exception e) {
                log.warn("Real email service failed, falling back to mock: {}", e.getMessage());
                // If real email service fails, try to use the mock service if available
                if (mockEmailService != null) {
                    mockEmailService.sendPasswordResetEmail(user.getEmail(), token);
                } else {
                    throw e; // Re-throw if mock service is not available
                }
            }
        } catch (Exception e) {
            log.error("Error sending password reset email", e);
            // Continue with the process even if email sending fails
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        // Find token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(InvalidTokenException::new);

        // Validate token
        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new InvalidTokenException();
        }

        // Find user
        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(TimestampUtils.now());
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }
}