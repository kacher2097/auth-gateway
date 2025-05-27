package com.authenhub.service;

import com.authenhub.bean.ChangePasswordRequest;
import com.authenhub.bean.ForgotPasswordRequest;
import com.authenhub.bean.OAuth2CallbackRequest;
import com.authenhub.bean.RegisterRequest;
import com.authenhub.bean.ResetPasswordRequest;
import com.authenhub.bean.SocialLoginRequest;
import com.authenhub.bean.auth.AuthRequest;
import com.authenhub.bean.auth.AuthResponse;
import com.authenhub.bean.auth.UserInfo;
import com.authenhub.bean.auth.social.SocialUserInfo;
import com.authenhub.bean.response.UserInfoResponse;
import com.authenhub.config.application.JsonMapper;
import com.authenhub.entity.Role;
import com.authenhub.entity.RolePermission;
import com.authenhub.entity.User;
import com.authenhub.entity.mongo.PasswordResetToken;
import com.authenhub.exception.ErrorApiException;
import com.authenhub.config.filter.JwtService;
import com.authenhub.repository.adapter.PasswordResetTokenRepositoryAdapter;
import com.authenhub.repository.jpa.PermissionJpaRepository;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.RolePermissionRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.interfaces.IAuthService;
import com.authenhub.utils.TimestampUtils;
import com.authenhub.utils.Utils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final JsonMapper jsonMapper;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleJpaRepository roleRepository;
    private final SocialLoginService socialLoginService;
    private final AuthenticationProvider authenticationManager;
//    private final AuthenticationProviderCustom authenticationManager;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordResetTokenRepositoryAdapter tokenRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Begin register new user with request {}", jsonMapper.toJson(request));
        // Kiểm tra username và email đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ErrorApiException("741", "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ErrorApiException("742", "Email already exists");
        }

        // Tạo user mới
        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole("USER");
        user.setRoleId(2L);
        user.setActive(true);
        user.setCreatedAt(TimestampUtils.now());
        user.setUpdatedAt(TimestampUtils.now());

        userRepository.saveAndFlush(user);
        log.info("Register new user successfully with id {}", user.getId());
        // Tạo token và response
        String token = jwtService.createToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(UserInfo.fromUser(user))
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) throws InvalidCredentialsException {
        log.info("Begin login with request {}", jsonMapper.toJson(request));

        // Lấy thông tin user
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ErrorApiException("234", "User không tồn tại"));
        // Xác thực thông tin đăng nhập
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user, StringUtils.EMPTY
                    )
            );
        } catch (Exception e) {
            log.error("Login error", e);
            throw new ErrorApiException("123", "Invalid username or password");
        }

        // Cập nhật thời gian đăng nhập
        user.setLastLogin(TimestampUtils.now());
        userRepository.save(user);
        log.info("Login user successfully with id {}", user.getId());
        // Tạo token và response
        String token = jwtService.createToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(UserInfo.fromUser(user))
                .build();
    }

    @Override
    public AuthResponse socialLogin(SocialLoginRequest request) {
        log.info("Begin social login with request {}", jsonMapper.toJson(request));
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
                    newUser.setRole("USER");
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
        String token = jwtService.createToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(UserInfo.fromUser(user))
                .build();
    }

    @Override
    public Object getCurrentUser(String token) {
        log.info("Begin get current user with token {}", token);
        // Lấy username từ token
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Role role = roleRepository.findById(user.getRoleId()).orElse(null);
        if (role != null) {
            List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleId(role.getId());
            if (rolePermissions == null || rolePermissions.isEmpty()) {
                return UserInfo.fromUser(user);
            }

            Set<String> permissions = rolePermissions.stream()
                    .map(RolePermission::getPermissionName)
                    .collect(Collectors.toSet());

            UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .avatar(user.getAvatar())
                    .roleId(user.getRoleId())
                    .permissions(permissions)
                    .build();
            log.info("UserInfoResponse have data {}", userInfoResponse);
            return userInfoResponse;
        }
        return UserInfo.fromUser(user);
    }

    @Override
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
                        newUser.setRole("USER");
                        newUser.setRoleId(2L);
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
            String token = jwtService.createToken(user);

            // Return response
            return AuthResponse.builder()
                    .token(token)
                    .user(UserInfo.fromUser(user))
                    .build();
        } catch (Exception e) {
            log.error("OAuth2 callback error", e);
            throw new RuntimeException("OAuth2 authentication failed: " + e.getMessage());
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ErrorApiException("123", "User with email not found"));

        // Delete any existing tokens for this user
//        tokenRepository.deleteByUserId(user.getId());

        // Generate token
        String token = Utils.generateToken();

        // Save token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
//        resetToken.setUserId(user.getId());
        resetToken.setExpiryDate(TimestampUtils.addHours(TimestampUtils.now(), 24)); // Token valid for 24 hours
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (Exception e) {
            log.error("Error sending password reset email", e);
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ErrorApiException("123", "Passwords do not match");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ErrorApiException("123", "Invalid token"));

        // Validate token
        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new ErrorApiException("123", "Token is invalid");
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

    @Override
    public void changePassword(ChangePasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ErrorApiException("123", "Passwords do not match");
        }

        // Get user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ErrorApiException("123", "Invalid current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(TimestampUtils.now());
        userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(String token) {
        log.info("Begin refresh token with token {}", token);
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ErrorApiException("123", "User không tồn tại"));
        String refreshToken = jwtService.createToken(user);
        log.info("Refresh token successfully with id {}", user.getId());
        return AuthResponse.builder()
                .token(refreshToken)
                .user(UserInfo.fromUser(user))
                .build();
    }
}