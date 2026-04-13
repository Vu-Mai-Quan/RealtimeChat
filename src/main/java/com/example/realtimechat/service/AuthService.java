package com.example.realtimechat.service;

import com.example.realtimechat.db1.model.NguoiDung.NguoiDungDangKi;
import com.example.realtimechat.db1.model.NguoiDung.NguoiDungLogin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.util.Map;

public interface AuthService {
    boolean signUp(NguoiDungDangKi nguoiDungDangKi);

    SignInResponse signIn(NguoiDungLogin login);

    boolean signOut(String accessToken);

    String createTokenFromRefreshToken(String refresh);

    @Builder
    record SignInResponse(String token, @JsonIgnore String refreshToken, Map<String, Object> userInfo) {
    }
}
