package com.authenhub.config.filter;

import com.authenhub.constant.Constant;
import com.authenhub.constant.JwtConstant;
import com.authenhub.dto.AccessLogDTO;
import com.authenhub.entity.Role;
import com.authenhub.entity.RolePermission;
import com.authenhub.entity.User;
import com.authenhub.event.AccessTrackingPublisher;
import com.authenhub.exception.ErrorApiException;
import com.authenhub.repository.jpa.RoleJpaRepository;
import com.authenhub.repository.jpa.RolePermissionRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.utils.TimestampUtils;
import com.authenhub.utils.Utils;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.authenhub.utils.Utils.getClientIp;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;
    private final AccessTrackingPublisher publisherAction;
    private final RolePermissionRepository rolePermissionRepository;

    // Danh sách các đường dẫn không cần xác thực
    private final static String[] PUBLIC_PATHS = {
            "/auth/login",
            "/auth/register",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/social-login",
            "/auth/oauth2/callback",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/api-docs"
    };

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        MDC.put(Constant.TOKEN, NanoIdUtils.randomNanoId());

        String requestPath = request.getRequestURI();
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String username;
        UsernamePasswordAuthenticationToken authToken;
        User user = null;
        try {
            if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = getJwtFromRequest(request);
            if (StringUtils.isEmpty(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authToken = getAuthorization(jwt);
                if (authToken != null) {
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    user = (User) authToken.getPrincipal();
                }
            }

            filterChain.doFilter(request, response);
        } catch (ErrorApiException ex) {
            log.error("Function doFilterInternal has exception: {}", ex.getMessage());
            throw new ErrorApiException(ex.getCode(), ex.getMessage());
        } finally {
            publishAction(request, user);
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
            String subject = validateClaims(claims);
            if (StringUtils.isEmpty(subject)) {
                log.error("Function getAuthentication FAIL -> Subject is empty!");
                throw new ErrorApiException("123", "Token is invalid");
            }

            // Get user from repository
            User user = userRepository.findByUsername(subject).orElse(null);
            if (Objects.isNull(user)) {
                log.error("User not found for username: {}", subject);
                throw new ErrorApiException("123", "User not found");
            }

            Set<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(claims, user, subject);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, token, grantedAuthorities);
//            authentication.setAuthenticated(true);
            log.debug("Authentication token created successfully for user: {}", subject);
            return authentication;
        } catch (ErrorApiException ex) {
            log.error("Function getAuthentication has exception: {}", ex.getMessage());
            throw ex;
        }
    }

    private String validateClaims(Claims claims) {
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
        return subject;
    }

    private Set<GrantedAuthority> getGrantedAuthorities(Claims claims, User user, String subject) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Long userId = claims.get(JwtConstant.USER_ID_FIELD, Long.class);
        Long roleId = claims.get(JwtConstant.JWT_TOKEN_CLAIM_ROLE_ID, Long.class);

        // Verify that the user ID in the token matches the user from the database
        if (!user.getId().equals(userId)) {
            log.debug("User ID mismatch: {} vs {}", user.getId(), userId);
            return Collections.emptySet();
        }

        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            log.debug("Role not found for id: {}", roleId);
            return Collections.emptySet();
        }

        List<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleId(roleId);
        if (rolePermissions.isEmpty()) {
            log.debug("RolePermission not found for id: {}", roleId);
            return Collections.emptySet();
        }

        Set<String> lstFunctionAction = rolePermissions.stream()
                .map(RolePermission::getPermissionName)
                .collect(Collectors.toSet());

        log.debug("Found {} permissions for user {}", lstFunctionAction.size(), subject);

        lstFunctionAction.forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission)));
        return grantedAuthorities;
    }

    private void publishAction(HttpServletRequest request, User user) {
        log.info("Begin publishAction from JWTFilter");
        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);
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
                .build();

        // Parse user agent for browser, OS, device type
        parseUserAgent(userAgent, accessLogDTO);
        publisherAction.publishEvent(accessLogDTO);
        log.info("End publish action from JWTFilter with data userName= {}, fullName = {}, mail= {}, ip = {}, uri = {} ",
                userLogin, fullName, mail, ip, uri);
    }

    private boolean isPublicPath(String requestPath) {
        // Check if the path starts with any of the public paths
        return Arrays.stream(PUBLIC_PATHS).anyMatch(requestPath::startsWith);
    }

    private void parseUserAgent(String userAgentString, AccessLogDTO accessLog) {
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        Browser browser = userAgent.getBrowser();
        OperatingSystem os = userAgent.getOperatingSystem();
        DeviceType deviceType = os.getDeviceType();

        accessLog.setBrowser(browser.getName());              // e.g. Chrome, Firefox
        accessLog.setOperatingSystem(os.getName());           // e.g. Windows, iOS
        accessLog.setDeviceType(deviceType.name());           // MOBILE, TABLET, COMPUTER, etc.
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return Constant.EMPTY_STRING;
    }
}