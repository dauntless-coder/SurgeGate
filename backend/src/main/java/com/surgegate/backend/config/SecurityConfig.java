package com.surgegate.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/buy", "/api/status/**", "/api/attendee/**", "/api/organizer/**", "/api/staff/**").permitAll()
                .anyRequest().authenticated()
            )
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}

