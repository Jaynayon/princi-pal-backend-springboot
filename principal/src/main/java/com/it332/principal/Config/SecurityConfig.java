package com.it332.principal.Config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
// @EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("janickamariealgonas@gmail.com");
        mailSender.setPassword("zmgq ocxa bopo wejk"); // Use your app password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Value("${base.url}")
    private String baseUrl;

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
    // Exception {
    // http
    // .cors() // Enable CORS
    // .and()
    // .csrf().disable() // Disable CSRF for simplicity (optional)
    // .authorizeHttpRequests(auth -> auth
    // // Allow preflight requests (OPTIONS) without authentication
    // .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    // // Swagger endpoints (optional, you can modify based on your need)
    // .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    // // You can allow some paths without authentication
    // .antMatchers("/auth/**").permitAll()
    // .anyRequest().authenticated()) // All other endpoints require authentication
    // .httpBasic(); // Enable basic auth (optional, depending on your needs)

    // return http.build();
    // }

    // CORS configuration
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Allow credentials (Authorization header, cookies, etc.)
        config.addAllowedOrigin(baseUrl); // Your frontend URL
        config.addAllowedHeader("*"); // Allow all headers
        config.addAllowedMethod("*"); // Allow all methods (GET, POST, PUT, DELETE, etc.)
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
