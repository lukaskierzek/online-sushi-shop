package com.sushiShop.onlineSushiShop.component;

import com.sushiShop.onlineSushiShop.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final String secretStringKey = "AVeryBigLongLongLongSecretKeyJWTLongLongLong";
    private final int expirationTimeOneHour = 1000 * 60 * 60;

    private SecretKey getSignKey() {
        SecretKey secretKey = Keys.hmacShaKeyFor(secretStringKey.getBytes(StandardCharsets.UTF_8));
        return secretKey;
    }

    public String generateToken(String username, Role userRole) {
        String jwts = Jwts.builder()
            .subject(username)
            .claim("role", userRole.name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationTimeOneHour))
            .signWith(getSignKey(), Jwts.SIG.HS256)
            .compact();

        return jwts;
    }

    private Claims parseToken(String token) {
        JwtParser parser = Jwts.parser()
            .verifyWith(getSignKey())
            .build();

        Claims claimJws = parser.parseSignedClaims(token).getPayload();
        return claimJws;
    }

    public String extractUsername(String token) {
        String subject = parseToken(token).getSubject();
        return subject;
    }

    public boolean isExpiredToken(String token) {
        boolean tokenExpired = parseToken(token).getExpiration().before(new Date());
        return tokenExpired;
    }

    public boolean validateToken(String token) {
        boolean tokenValidate = extractUsername(token) != null && !isExpiredToken(token);
        return tokenValidate;
    }
}

//TODO: Check later
