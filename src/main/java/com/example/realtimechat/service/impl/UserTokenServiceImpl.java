package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserTokenServiceImpl implements JwtService<UserDetails> {
    NguoiDungRepository nguoiDungRepository;
    Key jwtSecretKey, refreshTokenKey;
    long refreshExp, accessExp;

    public UserTokenServiceImpl(NguoiDungRepository nguoiDungRepository,
                                @Value("${token.access.key}") String jwtSecretKey,
                                @Value("${token.refresh.key}") String refreshTokenKey,
                                @Value("${token.refresh.exp}") long refreshExp,
                                @Value("${token.access.exp}") long accessExp) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.jwtSecretKey = Keys.hmacShaKeyFor(Base64.decodeBase64(jwtSecretKey));
        this.refreshTokenKey = Keys.hmacShaKeyFor(Base64.decodeBase64(refreshTokenKey));
        this.refreshExp = refreshExp * 60 * 60 * 1000L;
        this.accessExp = accessExp * 60 * 60 * 1000L;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Assert.notNull(userDetails, "UserDetails must not be null");
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", userDetails.getUsername());
        properties.put("roles", userDetails.getAuthorities());
        return Jwts.builder()
                .setClaims(properties)
                .signWith(jwtSecretKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExp))
                .setHeaderParam("typ", TokenType.ACCESS)
                .compact();
    }

    @Override
    public String generateRefreshToken(Properties properties) {
        Assert.notNull(properties, "properties must not be null");
        UUID id = UUID.fromString(properties.get("idUser").toString());
        Assert.notNull(id, "id User must not be null");
        var nd = nguoiDungRepository.findById(id);
        Assert.isTrue(nd.isPresent(), "User not found with id: " + id);
        return Jwts.builder()
                .setSubject(nd.get().getId().toString())
                .signWith(refreshTokenKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExp))
                .setHeaderParam("typ", TokenType.REFRESH)
                .setHeaderParam("version", nd.get().getTokenUserKey())
                .compact();
    }

    @Override
    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token);
    }

    @Override
    public String createTokenFromRefreshToken(String refreshToken) {
        var paseToken = Jwts.parserBuilder().setSigningKey(refreshTokenKey)
                .build()
                .parseClaimsJws(refreshToken);
        var id = paseToken.getBody().getSubject();
        var nd = nguoiDungRepository.findById(UUID.fromString(id));
        Assert.isTrue(nd.isPresent(), "User not found with id: " + id);
        if (!nd.get().getTokenUserKey().equals(UUID.fromString(paseToken.getHeader().get("version").toString()))) {
            throw new IllegalStateException("Refresh token is invalid for user with id: " + id);
        }
        return generateToken(nd.get());
    }

    private enum TokenType {
        ACCESS, REFRESH
    }
}
