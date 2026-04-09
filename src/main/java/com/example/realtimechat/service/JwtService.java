package com.example.realtimechat.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Properties;

public interface JwtService<T> {

    String generateToken(T userDetails);


    String generateRefreshToken(Properties properties);


    Jws<Claims> parseToken(String token);

    String createTokenFromRefreshToken(String refreshToken);


}
