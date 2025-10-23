package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import java.util.Arrays;

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the application.
 *
 * Supports multiple origins defined in environment variables or application.yml.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOriginsStr;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        String[] allowedOrigins = Arrays.stream(allowedOriginsStr.split(","))
                                        .map(String::trim)
                                        .toArray(String[]::new);

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry
                    .addMapping(SecurityConstants.API_V1_PREFIX + "/**")
                    .allowedOrigins(allowedOrigins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
