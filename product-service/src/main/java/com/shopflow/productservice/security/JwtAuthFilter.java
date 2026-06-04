package com.shopflow.productservice.security;

import io.lettuce.core.output.ScanOutput;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
// OncePerRequestFilter — runs ONCE per request
// Without this — filter might run multiple times
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthFilter.class);

    // JwtService — to validate and decode token
    private final JwtService jwtService;

    // RedisTemplate — to check if session exists
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtAuthFilter(JwtService jwtService, RedisTemplate<String, Object> redisTemplate) {
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,   // incoming request
            HttpServletResponse response, // outgoing response
            FilterChain filterChain       // chain of filters — pass to next
    ) throws ServletException, IOException {

        // ─── Step 1 — Extract Token from Header ─────
        // Looks for "Authorization: Bearer eyJhbGci..."
        String token = extractToken(request);

        // No token found — continue without authentication
        // Public routes will still work
        // Protected routes will be blocked by SecurityConfig
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // ─── Step 2 — Validate JWT ───────────────────
        // Check signature and expiry
        if (!jwtService.validateToken(token)) {
            // Invalid token — continue without authentication
            // SecurityConfig will block protected routes
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ─── Step 3 — Extract User Details ──────────
            // Decode token payload
            Long userId = jwtService.extractUserId(token);
            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            System.out.println("role"+role);
            // ─── Step 4 — Check Redis Session ───────────
            // Verify user is still logged in
            // If user logged out — session deleted from Redis
            // This prevents using valid tokens after logout
            String sessionKey = "Session:" + userId;
            Boolean sessionExists = redisTemplate.hasKey(sessionKey);

            if (Boolean.FALSE.equals(sessionExists)) {
                // Session not found — user logged out
                // Continue without authentication
                logger.warn("No session found for user: {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // ─── Step 5 — Set Authentication ────────────
            // Tell Spring Security who this user is
            // This is stored in SecurityContextHolder
            // Available throughout the request lifecycle
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,  // principal — who is this user
                            null,   // credentials — no password needed (JWT handles this)
                            List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            // ROLE_ADMIN or ROLE_CUSTOMER
                            // Spring Security uses ROLE_ prefix for hasRole() checks
                    );

            // Attach userId to authentication details
            // Accessible in controllers via SecurityContextHolder
            authentication.setDetails(userId);

            // Set in Security Context — now Spring Security knows this user
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            logger.debug("Authentication set for user: {} role: {}", email, role);

        } catch (Exception e) {
            logger.error("Cannot set authentication: {}", e.getMessage());
        }

        // ─── Step 6 — Continue to Next Filter ───────
        // Pass request to next filter or controller
        filterChain.doFilter(request, response);
    }

    // ─── Extract Token from Authorization Header ─
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check header exists and starts with "Bearer "
        if (StringUtils.hasText(bearerToken) &&
                bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // remove "Bearer " prefix
        }
        return null; // no token found
    }
}