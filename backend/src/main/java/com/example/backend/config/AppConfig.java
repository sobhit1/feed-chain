package com.example.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * General application-wide bean configuration.
 */
@Configuration
public class AppConfig {

    /**
     * Provides a singleton instance of BCryptPasswordEncoder as the primary password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides a singleton instance of ModelMapper for object-to-object mapping.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}