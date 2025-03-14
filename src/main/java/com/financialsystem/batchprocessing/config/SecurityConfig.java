package com.financialsystem.batchprocessing.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Lambda-style configuration for csrf
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/batch/**").hasRole("ADMIN")
                        .requestMatchers("/actuator/**").hasRole("MONITOR")
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}); // Lambda-style configuration for httpBasic

        // For H2 console (lambda-style configuration for headers and frameOptions)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("adminPass"))
                .roles("ADMIN", "MONITOR")
                .build();

        UserDetails monitor = User.builder()
                .username("monitor")
                .password(encoder.encode("monitorPass"))
                .roles("MONITOR")
                .build();

        return new InMemoryUserDetailsManager(admin, monitor);
    }
}