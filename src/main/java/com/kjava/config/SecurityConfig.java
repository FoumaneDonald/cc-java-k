package com.kjava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()

                // Profil et chefs-service accessibles à tous les connectés
                .requestMatchers("/employes/profil").authenticated()
                .requestMatchers("/employes/chefs-service").authenticated()

                // Liste de tous les employés : DIRECTEUR et CHEF_SERVICE
                .requestMatchers(HttpMethod.GET, "/employes/tous").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(HttpMethod.GET, "/employes/operants").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")

                // Gestion complète des employés : DIRECTEUR uniquement
                .requestMatchers("/employes/**").hasRole("DIRECTEUR")

                // Catégories : lecture par tous, écriture par DIRECTEUR et CHEF_SERVICE, suppression DIRECTEUR
                .requestMatchers(HttpMethod.GET,    "/categories/**").authenticated()
                .requestMatchers(HttpMethod.POST,   "/categories/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(HttpMethod.PUT,    "/categories/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("DIRECTEUR")

                // Échelons : même politique
                .requestMatchers(HttpMethod.GET,    "/echelons/**").authenticated()
                .requestMatchers(HttpMethod.POST,   "/echelons/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(HttpMethod.PUT,    "/echelons/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(HttpMethod.DELETE, "/echelons/**").hasRole("DIRECTEUR")

                // Contrats : tous les connectés
                .requestMatchers("/contrats/**").authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}