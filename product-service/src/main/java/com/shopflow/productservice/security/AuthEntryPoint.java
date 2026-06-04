package com.shopflow.productservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
// AuthenticationEntryPoint — called when unauthenticated user
// tries to access protected resource
public class AuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException  // reason for 401
    ) throws IOException {

        // Set response as JSON
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        // Build consistent error response
        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", 401);
        body.put("message", "Unauthorized — valid token required");
        body.put("error", "Unauthorized");
        body.put("path", request.getRequestURI());

        // Write JSON to response
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}