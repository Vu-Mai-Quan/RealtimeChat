package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.tomcat.util.codec.binary.Base64;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserTokenServiceImpl implements JwtService<UserDetails> {

    Key jwtSecretKey, refreshTokenKey;
    long refreshExp, accessExp;

    public UserTokenServiceImpl(
                                @Value("${token.access.key}") @NonNull String jwtSecretKey,
                                @Value("${token.refresh.key}") @NonNull String refreshTokenKey,
                                @Value("${token.refresh.exp}") long refreshExp,
                                @Value("${token.access.exp}") long accessExp) {

        this.jwtSecretKey = Keys.hmacShaKeyFor(Base64.encodeBase64(jwtSecretKey.getBytes(), true));
        this.refreshTokenKey = Keys.hmacShaKeyFor(Base64.encodeBase64(refreshTokenKey.getBytes(), true));
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
                .setHeaderParam(Header.TYPE, TokenType.ACCESS)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        Assert.notNull(userDetails, "properties must not be null");
        Assert.isInstanceOf(NguoiDung.class, userDetails, "userDetails must not be null");
        var nd = (NguoiDung) userDetails;
        return Jwts.builder()
                .setSubject(nd.getId().toString())
                .signWith(refreshTokenKey)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExp))
                .setHeaderParam(Header.TYPE, TokenType.REFRESH)
                .setHeaderParam("version", nd.getTokenUserKey())
                .compact();
    }

    @Override
    public Jws<Claims> parseToken(String token) {
        var parser =Jwts.parserBuilder();
//        var headerBody = token.split("\\.");
//        var tokenWithNoKey = (headerBody[0]+"."+headerBody[1]+".");
        var typeToken = TokenType.valueOf(parser.build().parse(token).getHeader().getType());
        return switch (typeToken) {
            case ACCESS -> parser.setSigningKey(this.jwtSecretKey).build().parseClaimsJws(token);
            case REFRESH -> parser.setSigningKey(this.refreshTokenKey).build().parseClaimsJws(token);
        };
    }



    public enum TokenType {
        ACCESS, REFRESH
    }
}
