package com.example.task.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * Filter to authenticate requests using JWT tokens
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtServiceAdapter jwtService;

    /**
     * Constant for the user ID attribute name in the request
     */
    public static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        // Skip filter if no Authorization header or it doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        jwt = authHeader.substring(7);
        
        try {
            logger.debug("Validating JWT token: {}", jwt);
            
            // Validate JWT token
            if (jwtService.isTokenValid(jwt)) {
                // Extract user ID from JWT token
                UUID userId = jwtService.extractUserId(jwt);
                logger.debug("Extracted user ID from JWT token: {}", userId);
                
                // Set user ID in request attributes
                request.setAttribute(USER_ID_ATTRIBUTE, userId);
                logger.debug("Set user ID in request attributes: {}", userId);
                
                // Create authentication token and set in security context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId.toString(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("Authentication set in SecurityContextHolder");
            } else {
                logger.warn("Invalid JWT token: {}", jwt);
            }
        } catch (Exception e) {
            // Log exception but don't throw it to allow the request to continue
            logger.error("JWT authentication failed", e);
        }
        
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}