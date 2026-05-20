package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.mapper.NguoiDungMapper;
import com.example.realtimechat.service.AuthService;
import com.example.realtimechat.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
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
    UserCache userCache;

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
    public SignInResponse signIn(NguoiDung.@NonNull NguoiDungLogin login) {
        Authentication au =
                authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getPassword()));
        var user = (NguoiDung) au.getPrincipal();
        return SignInResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .userInfo(Map.of("email", user.getUsername(), "displayName", user.getDisplayName()))
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
        var name = paseToken.getBody().getSubject();
        var cache = userCache.getUserFromCache(name);
        Optional<NguoiDung> nd = Optional.ofNullable(cache instanceof NguoiDung n ? n : null)
                .or(() -> nguoiDungRepository.findByEmail(name));
        Assert.isTrue(nd.isPresent(), "User not found with id: " + name);
        if (!nd.get().getTokenUserKey().equals(UUID.fromString(paseToken.getHeader().get("version").toString()))) {
            throw new IllegalStateException("Refresh token is invalid for user with id: " + name);
        }
        return jwtService.generateToken(nd.get());
    }

    @Override
    @PreAuthorize("authentication.name.equals(#nguoiDungLogin.email)")
    @Transactional(timeout = 3000)
    public boolean changeUserPassword(NguoiDung.@NonNull NguoiDungLogin nguoiDungLogin) {
        var nd = nguoiDungRepository.findByEmail(nguoiDungLogin.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));
        nd.setPassword(passwordEncoder.encode(nguoiDungLogin.getPassword()));
        nguoiDungRepository.save(nd);
        return false;
    }
}
