package com.example.realtimechat.config;

import com.example.realtimechat.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Map<HttpMethod, String[]> publicEndpoint = Map.of(
            POST, new String[]{
                    "/auth/sign-in",
                    "/auth/sign-up",
                    "/auth/refresh-token"
            },
            GET, new String[]{}
    );
    private final
    ObjectMapper objectMapper;

    @Bean
    SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http, JwtService<?> jwtService) throws Exception {
        var defaultPublic = new String[]{};
        return http.csrf(CsrfConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtFilter(jwtService, objectMapper),
                        UsernamePasswordAuthenticationFilter.class).authorizeHttpRequests(rq -> {
                    rq.requestMatchers(GET, publicEndpoint.getOrDefault(GET, defaultPublic)).permitAll();
                    rq.requestMatchers(POST, publicEndpoint.getOrDefault(HttpMethod.POST, defaultPublic)).permitAll();
                    rq.anyRequest().authenticated();
                }).exceptionHandling(ex -> {
                    ex.accessDeniedHandler(this::accessDeniedHandler);
                    ex.authenticationEntryPoint(this::authenticationEntryPoint);
                })
                .build();
    }

    private void authenticationEntryPoint(HttpServletRequest httpServletRequest,
                                          HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        extracted(httpServletRequest, e, httpServletResponse, "Bạn chưa xác thực", HttpStatus.UNAUTHORIZED);
    }

    private void accessDeniedHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                     AccessDeniedException e) throws IOException {

        extracted(httpServletRequest, e, httpServletResponse, "Bạn không có quyền truy" +
                " cập tài nguyên này", HttpStatus.FORBIDDEN);
    }

    private void extracted(@NonNull HttpServletRequest httpServletRequest, @NonNull Exception e,
                           @NonNull HttpServletResponse httpServletResponse, String baseMessage,
                           HttpStatus httpStatus) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, baseMessage);
        problemDetail.setProperty("error", e.getMessage());
        problemDetail.setProperty("path", httpServletRequest.getRequestURI());
        problemDetail.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));
        httpServletResponse.setStatus(problemDetail.getStatus());
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}
