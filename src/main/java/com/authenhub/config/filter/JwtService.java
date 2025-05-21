package com.authenhub.config.filter;

import com.authenhub.entity.User;
import com.authenhub.exception.ErrorApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.expiration-refresh-token}")
    private long refreshTokenExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from token: {}", token);
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    public Claims extractAllClaims(String token) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(token)) {
            log.error("Attempted to extract claims from null or empty token");
            return null;
        }

        try {
            final String finalToken = getJwtFromRequest(token);
            if (org.apache.commons.lang3.StringUtils.isEmpty(finalToken)) {
                log.error("Token after processing is null or empty");
                return null;
            }

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(finalToken)
                        .getBody();
                if (Objects.isNull(claims)) {
                    log.error("Claims is null");
                    throw new ErrorApiException("751", "Token Invalid");
                }
                if (claims.getExpiration().before(new Date())) {
                    log.error("Token is expired");
                    throw new ErrorApiException("754", "Token đã hết hạn, vui lòng đăng nhập lại");
                }

                return claims;
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.error("JWT token is expired: {}", e.getMessage());
                throw new ErrorApiException("754", "Token đã hết hạn, vui lòng đăng nhập lại");
            } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
                log.error("Invalid JWT signature: {}", e.getMessage());
                throw new ErrorApiException("751", "Token không hợp lệ");
            } catch (io.jsonwebtoken.UnsupportedJwtException e) {
                log.error("Unsupported JWT token: {}", e.getMessage());
                throw new ErrorApiException("751", "Token không được hỗ trợ");
            } catch (Exception e) {
                log.error("JWT token validation error: {}", e.getMessage());
                throw new ErrorApiException("751", "Token không hợp lệ");
            }
        } catch (ErrorApiException e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw e;
        }
    }

    public String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roleId", user.getRoleId());
        claims.put("userId", user.getId());
        if (user.getAvatar() != null) {
            claims.put("avatar", user.getAvatar());
        }

        if (user.getSocialProvider() != null) {
            claims.put("socialProvider", user.getSocialProvider());
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("refreshToken", true);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String getJwtFromRequest(String tokenWithBearer) {
        if (StringUtils.hasText(tokenWithBearer)) {
            if (tokenWithBearer.startsWith("Bearer ")) {
                String token = tokenWithBearer.substring(7).trim();
                log.debug("Extracted token: {}", token);
                return StringUtils.hasText(token) ? token : null;
            } else {
                return tokenWithBearer.trim();
            }
        }
        return null;
    }
}