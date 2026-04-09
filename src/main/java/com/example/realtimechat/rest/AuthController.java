package com.example.realtimechat.rest;

import com.example.realtimechat.db1.model.NguoiDung.NguoiDungLogin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/")
public class AuthController {

    @PostMapping("signup")
    ResponseEntity<?> signUp(NguoiDungLogin nguoiDungLogin) {
        return ResponseEntity.ok().build();
    }
}
