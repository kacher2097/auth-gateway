package com.authenhub.filter;

import com.authenhub.constant.Constant;
import com.authenhub.constant.JwtConstant;
import com.authenhub.entity.mongo.Permission;
import com.authenhub.entity.mongo.Role;
import com.authenhub.entity.mongo.User;
import com.authenhub.repository.PermissionRepository;
import com.authenhub.repository.RoleRepository;
import com.authenhub.repository.UserRepository;
import com.authenhub.utils.Utils;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) {
        long startTime = System.currentTimeMillis();
        MDC.put(Constant.TOKEN, NanoIdUtils.randomNanoId());
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = getJwtFromRequest(request);
            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = getAuthorization(jwt);
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Function doFilterInternal has exception: ", ex);
        } finally {
            log.info(
                    "Request to {} with ip=[{}] finish in {} ms",
                    Utils.getRequestUri(request),
                    Utils.getClientIp(request),
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
            String userId = claims.get(JwtConstant.USER_ID_FIELD, String.class);
            String roleId = claims.get(JwtConstant.JWT_TOKEN_CLAIM_ROLE_ID, String.class);

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

            Set<String> lstPermissionId = role.getPermissionIds();
            Set<String> lstFunctionAction = lstPermissionId.stream().map(permissionId -> {
                Permission permission = permissionRepository.findById(permissionId).orElse(null);
                if (permission == null) {
                    log.debug("Permission not found for id: {}", permissionId);
                    return null;
                }
                return permission.getName();
            }).collect(Collectors.toSet());
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

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken;
        }
        return Constant.EMPTY_STRING;
    }
}