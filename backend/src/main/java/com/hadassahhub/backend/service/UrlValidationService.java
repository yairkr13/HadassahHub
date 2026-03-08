package com.hadassahhub.backend.service;

import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service for validating and sanitizing URLs for external links.
 * Provides basic URL format checking and accessibility validation for the MVP scope.
 */
@Service
public class UrlValidationService {
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$"
    );
    
    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");
    private static final Set<String> BLOCKED_DOMAINS = Set.of(
        "localhost", "127.0.0.1", "0.0.0.0", "::1"
    );
    
    /**
     * Validates URL format and basic safety checks.
     */
    public boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Basic format validation
            if (!URL_PATTERN.matcher(url.trim()).matches()) {
                return false;
            }
            
            // Parse URL for detailed validation
            URI uri = URI.create(url.trim());
            URL parsedUrl = uri.toURL();
            
            // Check scheme
            if (!ALLOWED_SCHEMES.contains(parsedUrl.getProtocol().toLowerCase())) {
                return false;
            }
            
            // Check for blocked domains
            String host = parsedUrl.getHost();
            if (host != null && BLOCKED_DOMAINS.contains(host.toLowerCase())) {
                return false;
            }
            
            return true;
            
        } catch (MalformedURLException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Sanitizes URL by trimming whitespace and ensuring proper format.
     */
    public String sanitizeUrl(String url) {
        if (url == null) {
            return null;
        }
        
        String sanitized = url.trim();
        
        // Ensure URL has a scheme
        if (!sanitized.startsWith("http://") && !sanitized.startsWith("https://")) {
            sanitized = "https://" + sanitized;
        }
        
        return sanitized;
    }
    
    /**
     * Checks if URL is accessible (basic connectivity test).
     * Note: This is a simple implementation for MVP. In production, consider async processing.
     */
    public boolean isUrlAccessible(String url) {
        if (!isValidUrl(url)) {
            return false;
        }
        
        try {
            URL urlObj = URI.create(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000); // 5 seconds
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(true);
            
            // Set user agent to avoid blocking
            connection.setRequestProperty("User-Agent", "HadassahHub-ResourceValidator/1.0");
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            // Consider 2xx and 3xx as accessible
            return responseCode >= 200 && responseCode < 400;
            
        } catch (Exception e) {
            // If we can't connect, consider it inaccessible
            return false;
        }
    }
    
    /**
     * Validates URL format, safety, and accessibility.
     */
    public UrlValidationResult validateUrl(String url) {
        if (!isValidUrl(url)) {
            return new UrlValidationResult(false, false, "Invalid URL format or unsafe domain");
        }
        
        boolean accessible = isUrlAccessible(url);
        String message = accessible ? "URL is valid and accessible" : "URL is valid but not accessible";
        
        return new UrlValidationResult(true, accessible, message);
    }
    
    /**
     * Result of URL validation containing validity, accessibility, and message.
     */
    public record UrlValidationResult(
        boolean isValid,
        boolean isAccessible,
        String message
    ) {
        public boolean isFullyValid() {
            return isValid && isAccessible;
        }
    }
}