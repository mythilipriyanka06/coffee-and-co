package com.coffeeandco.cafe.config;

import com.coffeeandco.cafe.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * BCrypt password encoder bean — used by UserService and CustomUserDetailsService.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider that uses our CustomUserDetailsService + BCrypt.
     * This is available for Spring Security's internal mechanisms if needed.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Security filter chain configuration.
     *
     * Strategy: Permit all requests at the Spring Security layer.
     * The actual authorization (role-based access, session checks) is handled
     * by the existing AuthInterceptor (HandlerInterceptor) which checks the
     * HttpSession user and role. This avoids conflicts between Spring Security's
     * SecurityContext and the app's custom session-based authentication.
     *
     * Spring Security's role here:
     *  - Prevents Spring Boot's default auto-lockout (generated password)
     *  - Provides BCrypt PasswordEncoder and DaoAuthenticationProvider beans
     *  - Disables HTTP Basic and form login auto-configs
     *  - Disables CSRF (app uses custom session management)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable Spring Security's built-in form login — we use our own /login
            .formLogin(form -> form.disable())

            // Disable HTTP Basic auth
            .httpBasic(basic -> basic.disable())

            // Disable CSRF — the app uses custom session-based auth via HttpSession.
            // All sensitive operations are protected by AuthInterceptor session checks.
            .csrf(csrf -> csrf.disable())

            // Permit all requests — AuthInterceptor handles role-based access control
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Disable Spring Security's logout — handled by /logout in AuthController
            .logout(logout -> logout.disable());

        return http.build();
    }
}


