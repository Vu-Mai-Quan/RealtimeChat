package com.example.realtimechat.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface JwtService<T> {

    String generateToken(T userDetails);


    String generateRefreshToken(T properties);


    Jws<Claims> parseToken(String token);


}
