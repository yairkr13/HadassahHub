package com.hadassahhub.backend.config;

import com.hadassahhub.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check if Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        jwt = authHeader.substring(7);
        
        try {
            // Extract email from JWT token
            userEmail = jwtService.extractEmail(jwt);

            // If email is present and no authentication is set in SecurityContext
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Validate the token
                if (jwtService.validateToken(jwt) && !jwtService.isTokenExpired(jwt)) {
                    
                    // Extract user role from token
                    String role = jwtService.extractRole(jwt).name();
                    
                    // Create authentication token with role-based authority
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );
                    
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                    userEmail, 
                                    null, 
                                    authorities
                            );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log the exception and continue without authentication
            logger.debug("JWT token validation failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}