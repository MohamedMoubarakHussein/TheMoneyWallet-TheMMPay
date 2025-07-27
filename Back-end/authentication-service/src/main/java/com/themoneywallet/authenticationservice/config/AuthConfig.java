package com.themoneywallet.authenticationservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.themoneywallet.authenticationservice.config.security.JwtAuthenticationFilter;
import com.themoneywallet.authenticationservice.service.implementation.CustomOAuth2UserService;
import com.themoneywallet.authenticationservice.service.implementation.MyUserDetailsService;
import com.themoneywallet.authenticationservice.utility.LoggingAccessDeniedHandler;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class AuthConfig {

    private final JwtAuthenticationFilter authenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final MyUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(req -> req.anyRequest().permitAll()
                .requestMatchers("/error","/ws/**","/auth/signup", "/auth/signin", "/oauth2/**", "/login/oauth2/**" , "/actuator/**" ,"/auth/v3/api-docs").permitAll()
                .anyRequest().authenticated()
            )
             .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2SuccessHandler)
            )
            .authenticationProvider(this.authenticationProvider())
            .addFilterBefore(
                this.authenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            ) .exceptionHandling( i ->  i.accessDeniedHandler(new LoggingAccessDeniedHandler()))
            .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(this.userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}