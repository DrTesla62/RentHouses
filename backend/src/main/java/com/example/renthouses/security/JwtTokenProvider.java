package com.example.renthouses.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.example.renthouses.entity.User;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final int jwtExpirationInMs;

    public JwtTokenProvider(
            @Value("${app.jwtSecret}") String jwtSecret,
            @Value("${app.jwtExpirationInMs}") int jwtExpirationInMs) {
        String encodedSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.key = Keys.hmacShaKeyFor(encodedSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();

        // Use the role name directly from the database
        String roleName = user.getRole().getName();
        claims.put("roles", java.util.Collections.singletonList(roleName));
        claims.put("roleId", user.getRole().getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}