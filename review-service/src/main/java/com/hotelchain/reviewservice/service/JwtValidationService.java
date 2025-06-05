package com.hotelchain.reviewservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Service
public class JwtValidationService {

    private static final String SECRET = "mySecretKeyForHotelChainApplicationThatIsLongEnough";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Claims validateToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateClientRole(String token) {
        Claims claims = validateToken(token);
        String role = claims.get("role", String.class);

        if (!"CLIENT".equals(role)) {
            throw new RuntimeException("Access denied - Client role required");
        }
    }

    public void validateManagerRole(String token) {
        Claims claims = validateToken(token);
        String role = claims.get("role", String.class);

        if (!"MANAGER".equals(role) && !"ADMIN".equals(role)) {
            throw new RuntimeException("Access denied - Manager role required");
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Long.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }

    public Long getHotelIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("hotelId", Long.class);
    }

    // Additional helper methods
    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}