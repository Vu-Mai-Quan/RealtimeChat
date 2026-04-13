package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.mapper.NguoiDungMapper;
import com.example.realtimechat.service.AuthService;
import com.example.realtimechat.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    NguoiDungRepository nguoiDungRepository;
    NguoiDungMapper nguoiDungMapper;
    JwtService<UserDetails> jwtService;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;

    @Override
    public boolean signUp(NguoiDung.NguoiDungDangKi nguoiDungDangKi) {
        try {
            NguoiDung nd = nguoiDungMapper.dangKiMapper(nguoiDungDangKi);
            nd.setPassword(passwordEncoder.encode(nguoiDungDangKi.getPassword()));
            nguoiDungRepository.save(nd);
            return true;
        } catch (JpaSystemException e) {
            log.error("Error during sign up: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public SignInResponse signIn(NguoiDung.NguoiDungLogin login) {
        Authentication au =
                authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getPassword()));
        var user = (NguoiDung) au.getPrincipal();
        return SignInResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .userInfo(Map.of("email", user.getUsername()))
                .build();
    }

    @Override
    public boolean signOut(String accessToken) {
        try {
            var id = UUID.fromString(jwtService.parseToken(accessToken).getBody().getSubject());
            var newKey = UUID.randomUUID();
            nguoiDungRepository.updateVersionToken(id, newKey);
            return true;
        } catch (JwtException | EntityNotFoundException e) {
            log.error("Error during sign out: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String createTokenFromRefreshToken(String refreshToken) {
        var paseToken = jwtService.parseToken(refreshToken);
        var id = paseToken.getBody().getSubject();
        var nd = nguoiDungRepository.findById(UUID.fromString(id));
        Assert.isTrue(nd.isPresent(), "User not found with id: " + id);
        if (!nd.get().getTokenUserKey().equals(UUID.fromString(paseToken.getHeader().get("version").toString()))) {
            throw new IllegalStateException("Refresh token is invalid for user with id: " + id);
        }
        return jwtService.generateToken(nd.get());
    }
}
