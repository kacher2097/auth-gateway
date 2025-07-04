package com.authenhub.filter;

import com.authenhub.constant.Constant;
import com.authenhub.constant.JwtConstant;
import com.authenhub.dto.AccessLogDTO;
import com.authenhub.entity.RolePermission;
import com.authenhub.entity.User;
import com.authenhub.entity.Permission;
import com.authenhub.entity.Role;
import com.authenhub.event.AccessTrackingPublisher;
import com.authenhub.exception.ErrorApiException;
import com.authenhub.repository.jpa.PermissionJpaRepository;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.RolePermissionRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.utils.TimestampUtils;
import com.authenhub.utils.Utils;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.authenhub.utils.Utils.getClientIp;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;
    private final UserJpaRepository userJpaRepository;
    private final AccessTrackingPublisher publisherAction;
    private final PermissionJpaRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    // Danh sách các đường dẫn không cần xác thực
    private final static String[] PUBLIC_PATHS = {
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/social-login",
            "/auth/oauth2/callback"
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        MDC.put(Constant.TOKEN, NanoIdUtils.randomNanoId());

        String requestPath = request.getRequestURI();
        for (String publicPath : PUBLIC_PATHS) {
            if (requestPath.startsWith(publicPath)) {
                log.info("Request to {} kiểm tra và xử lí lỗi ở filter khi ng dùng đang đăng nhập", requestPath);
                filterChain.doFilter(request, response);
                return;
            }
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = getJwtFromRequest(request);
            if (jwt == null || jwt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = getAuthorization(jwt);
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            publishAction(request, username);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Function doFilterInternal has exception: ", ex);
        } finally {
            log.info(
                    "Request to {} with ip=[{}] finish in {} ms",
                    Utils.getRequestUri(request),
                    getClientIp(request),
                    Utils.end(startTime)
            );
            MDC.clear();
        }

    }

    public UsernamePasswordAuthenticationToken getAuthorization(String token) {
        try {
            Claims claims = jwtService.extractAllClaims(token);
            if (Objects.isNull(claims)) {
                log.info("Function getAuthentication FAIL -> Claims is empty!");
                return null;
            }

            if (!claims.containsKey(JwtConstant.JWT_TOKEN_CLAIM_SUBJECT)) {
                log.info("Function getAuthentication fail -> claim [sub] doesn't exist!");
                return null;
            }

            String subject = claims.getSubject();
            if (Strings.isBlank(subject)) {
                log.info("Function getAuthentication fail -> claim [sub] is empty!");
                return null;
            }
            if (null == claims.get(JwtConstant.JWT_TOKEN_CLAIM_ROLE_ID)) {
                log.info("Function getAuthentication fail -> claim [roleId] is empty!");
                return null;
            }

            // Get user from repository
            User user = userRepository.findByUsername(subject).orElse(null);
            if (user == null) {
                log.debug("User not found for username: {}", subject);
                return null;
            }

            // Get permissions
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            Long userId = claims.get(JwtConstant.USER_ID_FIELD, Long.class);
            Long roleId = claims.get(JwtConstant.JWT_TOKEN_CLAIM_ROLE_ID, Long.class);

            // Verify that the user ID in the token matches the user from the database
            if (!user.getId().equals(userId)) {
                log.debug("User ID mismatch: {} vs {}", user.getId(), userId);
                return null;
            }

            Role role = roleRepository.findById(roleId).orElse(null);
            if (role == null) {
                log.debug("Role not found for id: {}", roleId);
                return null;
            }

            List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleId(roleId);
            if (rolePermissions == null || rolePermissions.isEmpty()) {
                log.debug("RolePermission not found for id: {}", roleId);
                return null;
            }

            Set<String> lstFunctionAction = rolePermissions.stream()
                    .map(RolePermission::getPermissionName)
                    .collect(Collectors.toSet());

            log.debug("Found {} permissions for user {}", lstFunctionAction.size(), subject);

            lstFunctionAction.forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission)));

            // Create authentication token with the full User object as principal
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, token, grantedAuthorities);
            log.debug("Authentication token created successfully for user: {}", subject);
            return authentication;
        } catch (Exception ex) {
            log.error("Function getAuthentication has exception: ", ex);
            return null;
        }
    }

    private void publishAction(HttpServletRequest request, String username) throws ErrorApiException, ExecutionException {
        log.info("Begin publishAction from JWTFilter");
        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String ip = getClientIp((request));
        com.authenhub.entity.User user = getUserInfo(username);
        String userLogin = null;
        String fullName = null;
        String mail = null;
        if (Objects.nonNull(user)) {
            userLogin = user.getUsername();
            fullName = user.getFullName();
            mail = user.getEmail();
        }
        String userAgent = request.getHeader("User-Agent");
        AccessLogDTO accessLogDTO = AccessLogDTO.builder()
                .username(userLogin)
                .ipAddress(ip)
                .endpoint(uri)
                .userAgent(userAgent)
                .method(request.getMethod())
                .timestamp(TimestampUtils.now())
                .referrer(request.getHeader("Referer"))
                .responseTimeMs(System.currentTimeMillis() - startTime)
//                .sessionId(request.getSession().getId())
                .build();

        // Parse user agent for browser, OS, device type
        parseUserAgent(userAgent, accessLogDTO);
        publisherAction.publishEvent(accessLogDTO);
        log.info("End publish action from JWTFilter with data userName= {}, fullName = {}, mail= {}, ip = {}, uri = {} ",
                userLogin, fullName, mail, ip, uri);
    }

    private User getUserInfo(String username) {
        User user = userJpaRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.debug("User not found for username: {}", username);
            return null;
        }
        return user;
    }

    private void parseUserAgent(String userAgent, AccessLogDTO accessLog) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(userAgent)) {
            return;
        }

        // Simple parsing - in a real app you might use a library like UADetector or user-agent-utils
        userAgent = userAgent.toLowerCase();

        // Detect browser
        if (userAgent.contains("firefox") || userAgent.equalsIgnoreCase("firefox")) {
            accessLog.setBrowser("Firefox");
        } else if (userAgent.contains("chrome") && !userAgent.contains("edge")) {
            accessLog.setBrowser("Chrome");
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            accessLog.setBrowser("Safari");
        } else if (userAgent.contains("edge") || userAgent.contains("edg")) {
            accessLog.setBrowser("Edge");
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            accessLog.setBrowser("Opera");
        } else {
            accessLog.setBrowser("Other");
        }

        // Detect OS
        if (userAgent.contains("windows")) {
            accessLog.setOperatingSystem("Windows");
        } else if (userAgent.contains("mac os")) {
            accessLog.setOperatingSystem("MacOS");
        } else if (userAgent.contains("linux")) {
            accessLog.setOperatingSystem("Linux");
        } else if (userAgent.contains("android")) {
            accessLog.setOperatingSystem("Android");
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            accessLog.setOperatingSystem("iOS");
        } else {
            accessLog.setOperatingSystem("Other");
        }

        // Detect device type
        if (userAgent.contains("mobile") || userAgent.contains("iphone")) {
            accessLog.setDeviceType("MOBILE");
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            accessLog.setDeviceType("TABLET");
        } else {
            accessLog.setDeviceType("DESKTOP");
        }
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return Constant.EMPTY_STRING;
    }
}