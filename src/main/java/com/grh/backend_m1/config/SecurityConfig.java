package com.grh.backend_m1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Indispensable pour tester les POST plus tard
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // On ouvre toutes les portes pour le développement
            );
        return http.build();
    }
}