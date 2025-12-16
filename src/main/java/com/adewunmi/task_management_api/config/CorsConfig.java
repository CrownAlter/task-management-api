package com.adewunmi.task_management_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for Task Management API
 * Configures Cross-Origin Resource Sharing policies for the application
 * 
 * Best Practices for Production:
 * - Never use "*" for allowed-origins when credentials are enabled
 * - Specify exact domains for security
 * - Use environment variables to configure different origins per environment
 */
@Configuration
public class CorsConfig {
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;
    
    @Value("${app.cors.exposed-headers:Authorization,Content-Type}")
    private String exposedHeaders;

    @Value("${app.cors.max-age:3600}")
    private Long maxAge;
    
    @Value("${app.cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configure allowed origins
        if ("*".equals(allowedOrigins)) {
            // Use allowedOriginPattern for wildcard support with credentials
            configuration.addAllowedOriginPattern("*");
        } else {
            // Split comma-separated origins and trim whitespace
            List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
            configuration.setAllowedOrigins(origins);
        }

        // Configure allowed methods
        if ("*".equals(allowedMethods)) {
            configuration.addAllowedMethod("*");
        } else {
            List<String> methods = Arrays.stream(allowedMethods.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
            configuration.setAllowedMethods(methods);
        }

        // Configure allowed headers
        if ("*".equals(allowedHeaders)) {
            configuration.addAllowedHeader("*");
        } else {
            List<String> headers = Arrays.stream(allowedHeaders.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
            configuration.setAllowedHeaders(headers);
        }
        
        // Configure exposed headers (headers the browser can access)
        if (exposedHeaders != null && !exposedHeaders.isEmpty()) {
            List<String> headers = Arrays.stream(exposedHeaders.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
            configuration.setExposedHeaders(headers);
        }

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(allowCredentials);
        
        // Cache preflight requests for the specified duration (in seconds)
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
