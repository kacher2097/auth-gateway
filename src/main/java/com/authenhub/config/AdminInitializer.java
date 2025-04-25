package com.authenhub.config;

import com.authenhub.entity.User;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.fullName}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        // Kiểm tra xem đã có admin chưa
        if (!userRepository.existsByUsername(adminUsername)) {
            // Tạo tài khoản admin mặc định
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFullName(adminFullName);
            admin.setRole("ADMIN");
            admin.setActive(true);
            admin.setCreatedAt(TimestampUtils.now());
            admin.setUpdatedAt(TimestampUtils.now());

            userRepository.save(admin);
            System.out.println("Đã tạo tài khoản admin mặc định");
        }
    }
}