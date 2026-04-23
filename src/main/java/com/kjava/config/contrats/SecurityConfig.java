package com.kjava.config.contrats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                // Employé : profil et chefs-service accessibles à tous les authentifiés
                .requestMatchers("/employes/profil").authenticated()
                .requestMatchers("/employes/chefs-service").authenticated()
                // Lister tous les employés (pour le formulaire contrat) : DIRECTEUR et CHEF_SERVICE
                .requestMatchers("/employes/operants").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                // Lister TOUS les employés pour la sélection dans le formulaire contrat
                .requestMatchers("/employes/tous").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers("/employes/**").hasRole("DIRECTEUR")
                // Catégories et échelons : lecture par tous les authentifiés (GET), écriture par DIRECTEUR et CHEF_SERVICE
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/categories/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/echelons/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/categories/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/categories/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/categories/**").hasRole("DIRECTEUR")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/echelons/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/echelons/**").hasAnyRole("DIRECTEUR", "CHEF_SERVICE")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/echelons/**").hasRole("DIRECTEUR")
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