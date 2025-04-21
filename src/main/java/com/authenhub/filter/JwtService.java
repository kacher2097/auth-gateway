package com.authenhub.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.authenhub.entity.mongo.User;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        if (roles != null && !roles.isEmpty()) {
            // Return the first role (assuming a user has only one role)
            return roles.get(0);
        }
        return null;
    }

    public boolean hasRole(String token, String role) {
        Claims claims = extractAllClaims(token);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        return roles != null && roles.contains(role);
    }

    public Claims extractAllClaims(String token) {
        final String finalToken = getJwtFromRequest(token);
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(finalToken)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Add user roles and permissions to the token
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("roles", authorities.stream()
                .filter(auth -> auth.startsWith("ROLE_"))
                .toList());

        claims.put("roleId", authorities.stream()
                .filter(auth -> auth.startsWith("ROLE_"))
                .toList());

        // Add permissions separately for easier access
        claims.put("permissions", authorities.stream()
                .filter(auth -> !auth.startsWith("ROLE_"))
                .toList());

        // Add additional user info if available
        if (userDetails instanceof User user) {
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());
            claims.put("fullName", user.getFullName());

            if (user.getAvatar() != null) {
                claims.put("avatar", user.getAvatar());
            }

            if (user.getSocialProvider() != null) {
                claims.put("socialProvider", user.getSocialProvider());
            }
        }

        return createToken(claims, userDetails);
    }

    public String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roleId", user.getRoleId());
        claims.put("userId", user.getId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }

    private String createToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }

//    public String generateRefreshToken(User user) {
//        return Jwts.builder()
//                .setSubject(user.getEmail())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 2592000000L)) // 30 days
//                .signWith(getSigningKey())
//                .compact();
//    }

    private String getJwtFromRequest(String tokenWithBearer) {
        if (StringUtils.hasText(tokenWithBearer) && tokenWithBearer.startsWith("Bearer ")) {
            String token = tokenWithBearer.substring(7).trim();
            log.debug("Extracted token: {}", token);
            return StringUtils.hasText(token) ? token : null;
        }
        return null;
    }

    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}