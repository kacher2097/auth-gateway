package com.authenhub.config.security;

import com.authenhub.constant.JwtConstant;
import com.authenhub.entity.User;
import com.authenhub.filter.JwtService;
import com.authenhub.repository.jpa.UserJpaRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserJpaRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 authentication successful");

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            if (email != null) {
                Optional<User> userOptional = userRepository.findByEmail(email);
                User user;

                if (userOptional.isPresent()) {
                    user = userOptional.get();
                    log.info("Existing user found with email: {}", email);
                } else {
                    // Create a new user if not exists
                    user = createNewUser(email, name);
                    log.info("Created new user with email: {}", email);
                }

                // Generate JWT token
                String token = jwtService.createToken(user);

                // Redirect to frontend with token
                String redirectUrl = "/auth/oauth2/callback?token=" + token;
                response.sendRedirect(redirectUrl);
                return;
            }
        }

        // Fallback redirect if something went wrong
        response.sendRedirect("/auth/login?error=oauth2_failed");
    }

    private User createNewUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email); // Use email as username
        user.setFullName(name != null ? name : "OAuth User");
        user.setRoleId(2L); // Default role ID for OAuth users
        user.setActive(true);

        return userRepository.save(user);
    }
}
