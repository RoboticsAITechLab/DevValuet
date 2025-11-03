package com.devvault.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration that secures management (actuator) endpoints.
 * - /actuator/** requires ROLE_ACTUATOR
 * - other application endpoints are left authenticated / permitAll as appropriate.
 *
 * Note: credentials are controlled via `spring.security.user.*` properties for a simple, local setup.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // disable CSRF for simplicity for non-browser clients; adjust if needed
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // secure actuator endpoints to users with ACTUATOR role
                .requestMatchers("/actuator/**").hasRole("ACTUATOR")
                // allow public access to simple health endpoint if desired
                // .requestMatchers("/actuator/health").permitAll()
                // keep other endpoints open (or change to authenticated() for stricter security)
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
