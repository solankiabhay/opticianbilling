package com.surya.optics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${APP_USER:admin}") private String username;
    @Value("${APP_PASSWORD:}") private String password;

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        if (password == null || password.trim().length() < 12) {
            throw new IllegalStateException("Set APP_PASSWORD to a strong password of at least 12 characters before starting the application.");
        }
        return new InMemoryUserDetailsManager(
            User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build()
        );
    }

    @Bean public org.springframework.security.web.SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/error").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().permitAll()
            .and().logout().logoutSuccessUrl("/login?logout").permitAll();
        return http.build();
    }
}
