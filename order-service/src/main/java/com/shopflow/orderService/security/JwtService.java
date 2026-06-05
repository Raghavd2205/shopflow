package com.shopflow.orderService.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component  // ← marks as Spring managed bean — can be injected anywhere
public class JwtService {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtService.class);

    // Read JWT_SECRET from application.properties
    // Same secret as User Service — that is how we verify their tokens
    @Value("${jwt.secret}")
    private String jwtSecret;

    // ─── Extract All Claims ──────────────────────
    // Claims = payload inside JWT token
    // { userId: 1, email: "r@g.com", role: "admin" }
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes()) // use secret to verify signature
                .build()
                .parseClaimsJws(token)   // parse and verify token
                .getBody();              // get payload
    }

    // ─── Extract UserId ──────────────────────────
    // Gets userId from token payload
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    // ─── Extract Email ───────────────────────────
    // Gets email from token payload
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    // ─── Extract Role ────────────────────────────
    // Gets role from token payload — admin or customer
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    // ─── Validate Token ──────────────────────────
    // Returns true if token is valid
    // Returns false if expired, malformed, or tampered
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true; // ← token is valid ✅

        } catch (ExpiredJwtException e) {
            // Token valid but expired — 15 min passed
            logger.error("JWT token expired: {}", e.getMessage());

        } catch (MalformedJwtException e) {
            // Token structure is broken — tampered
            logger.error("Invalid JWT token: {}", e.getMessage());

        } catch (UnsupportedJwtException e) {
            // Token algorithm not supported
            logger.error("Unsupported JWT token: {}", e.getMessage());

        } catch (IllegalArgumentException e) {
            // Token is empty or null
            logger.error("JWT claims empty: {}", e.getMessage());
        }
        return false; // ← token is invalid ❌
    }
}