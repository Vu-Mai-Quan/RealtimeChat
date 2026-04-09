package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.mapper.NguoiDungMapper;
import com.example.realtimechat.service.AuthService;
import com.example.realtimechat.service.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    NguoiDungRepository nguoiDungRepository;
    NguoiDungMapper nguoiDungMapper;
    JwtService<UserDetails> jwtService;
    PasswordEncoder passwordEncoder;

    @Override
    public boolean signUp(NguoiDung.NguoiDungDangKi nguoiDungDangKi) {
        try {
            NguoiDung nd = nguoiDungMapper.dangKiMapper(nguoiDungDangKi);
            nd.setPassword(passwordEncoder.encode(nd.getPassword()));
            nguoiDungRepository.save(nd);
            return true;
        } catch (JpaSystemException e) {
            log.error("Error during sign up: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public SignInResponse signIn(NguoiDung.NguoiDungLogin login) {
        return null;
    }
}
