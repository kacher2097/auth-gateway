package com.authenhub.config;


import com.authenhub.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                    requests -> requests
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository clientRegistrationRepository() {
//        return new org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository(
//                org.springframework.security.oauth2.client.registration.CommonOAuth2Provider.GOOGLE.getBuilder("google")
//                        .clientId("${GOOGLE_CLIENT_ID}")
//                        .clientSecret("${GOOGLE_CLIENT_SECRET}")
//                        .build(),
//                org.springframework.security.oauth2.client.registration.CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
//                        .clientId("${FACEBOOK_CLIENT_ID}")
//                        .clientSecret("${FACEBOOK_CLIENT_SECRET}")
//                        .build()
//        );
//    }
} 