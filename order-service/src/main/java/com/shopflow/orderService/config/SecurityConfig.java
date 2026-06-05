package com.shopflow.orderService.config;

import com.shopflow.orderService.security.AccessDeniedHandlerImpl;
import com.shopflow.orderService.security.AuthEntryPoint;
import com.shopflow.orderService.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity   // ← enables Spring Security
@EnableMethodSecurity // ← enables @PreAuthorize on methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthEntryPoint authEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for REST APIs
                // CSRF is for browser based form submissions
                // JWT handles our security
                .csrf(csrf -> csrf.disable())

                // STATELESS — no server side sessions
                // Every request must carry JWT token
                // No cookies, no sessions stored on server
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Custom error handlers
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)    // handles 401
                        .accessDeniedHandler(accessDeniedHandler)    // handles 403
                )

                .authorizeHttpRequests(auth -> auth

                        // ─── PUBLIC — No token needed ────────────
                        // Anyone can browse products and categories
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/product/**",
                                "/api/v1/category/**",
                                "/health"
                        ).permitAll()

                        // ─── ADMIN ONLY ──────────────────────────
                        // Only users with ROLE_ADMIN can do these
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/product/**",
                                "/api/v1/category/**"
                        ).hasRole("ADMIN")  // ← checks ROLE_ADMIN in SecurityContext

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/product/**",
                                "/api/v1/category/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/product/**",
                                "/api/v1/category/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/v1/product/**"
                        ).hasRole("ADMIN")

                        // Everything else needs authentication
                        .anyRequest().authenticated()
                )

                // Add our JWT filter BEFORE Spring's default auth filter
                // So JWT runs first — sets authentication
                // Then Spring's filter sees authenticated user
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}