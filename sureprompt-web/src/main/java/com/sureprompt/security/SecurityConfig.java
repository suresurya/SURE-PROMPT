package com.sureprompt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .requiresChannel(channel -> channel.anyRequest().requiresSecure())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(org.springframework.security.config.Customizer.withDefaults())
                .httpStrictTransportSecurity(org.springframework.security.config.Customizer.withDefaults())
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/v1/feed", "/api/v1/search/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .requiresChannel(channel -> channel.anyRequest().requiresSecure())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(org.springframework.security.config.Customizer.withDefaults())
                .httpStrictTransportSecurity(org.springframework.security.config.Customizer.withDefaults())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/explore", "/prompts/**", "/profile/**", "/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/login", "/logout"));

        return http.build();
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://sureprompt.com")); // Lock down to prod domain
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "No-Authentication"));
        config.setExposedHeaders(List.of("X-Request-ID"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
