package com.authenhub.filter;

import com.authenhub.constant.JwtConstant;
import com.authenhub.entity.Permission;
import com.authenhub.repository.PermissionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PermissionRepository permissionRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
//        jwt = getJwtFromRequest(request);

//        if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
//            username = jwtService.getUsernameFromJWT(jwt);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }

            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );
                    UsernamePasswordAuthenticationToken authToken = getAuthorization(jwt);
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }catch (Exception ex) {
            log.error("Function doFilterInternal has exception: ", ex);
//            response(response, BaseResponse.of(BaseResponseCode.FORBIDDEN));
        } finally {
//            log.info(
//                    "Request to {} with ip=[{}] finish in {} ms",
//                    getRequestUri(request),
//                    getClientIp(request),
//                    end(startTime)
//            );
            MDC.clear();
        }

    }

    public UsernamePasswordAuthenticationToken getAuthorization(String token) {
        try {
            log.debug("Function getAuthentication start -> input data=[token=[{}]]", token);

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

            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            String userId = claims.get(JwtConstant.USER_ID_FIELD, String.class);
            String roleId = claims.get(JwtConstant.JWT_TOKEN_CLAIM_ROLE_ID, String.class);
            List<Permission> lstPermission = permissionRepository.findAllById(roleId);
            List<String> lstFunctionAction = lstPermission.stream().map(Permission::getName).toList();

            lstFunctionAction.forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission)));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    subject, token, grantedAuthorities);
            log.info("Function getAuthentication end!");
            return authentication;
        } catch (Exception ex) {
            log.error("Function getAuthentication has exception: ", ex);
            return null;
        }
    }

//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter();
//    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 