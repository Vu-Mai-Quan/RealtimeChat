package com.example.realtimechat.config;

import com.example.realtimechat.service.JwtService;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtService<?> jwtService;
    private final ObjectMapper objectMapper;
    private final Set<String> excludedPaths = Set.of(
            "/api/auth/sign-in",
            "/api/auth/sign-up",
            "/api/auth/refresh-token"
    );

    public JwtFilter(JwtService<?> jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludedPaths.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    var auth = authentication(token, request);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response);
                }
            } catch (JwtException | JacksonException e) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid or " +
                        "expired token");
                problemDetail.setProperty("error", e.getMessage());
                problemDetail.setProperty("path", request.getRequestURI());
                problemDetail.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));
                response.setStatus(problemDetail.getStatus());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private @NonNull Authentication authentication(final String token, HttpServletRequest req) {
        var body = jwtService.parseToken(token).getBody();
        Set<SimpleGrantedAuthority> roles = objectMapper.convertValue(body.get("roles") == null ? Set.of() :
                        body.get("roles"),
                new TypeReference<Set<String>>() {
                }).stream().map(SimpleGrantedAuthority::new).collect(java.util.stream.Collectors.toSet());
        var auth = new UsernamePasswordAuthenticationToken(
                body.getSubject(),
                token,
                roles.isEmpty() ? Set.of() : roles
        );
        auth.setDetails(new WebAuthenticationDetails(req));
        return auth;
    }

    ;
}
