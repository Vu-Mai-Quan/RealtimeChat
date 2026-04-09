package com.example.realtimechat.service;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.model.NguoiDung.NguoiDungLogin;

import java.util.Map;

public interface AuthService {
    boolean signUp(NguoiDung.NguoiDungDangKi nguoiDungDangKi);

    SignInResponse signIn(NguoiDungLogin login);

    record SignInResponse(String token, String refreshToken, Map<String, Object> userInfo) {
    }
}
