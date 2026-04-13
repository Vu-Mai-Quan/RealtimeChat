package com.example.realtimechat.rest;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.model.NguoiDung.NguoiDungLogin;
import com.example.realtimechat.service.AuthService;
import com.example.realtimechat.validations.GroupValidation;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.Date;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static com.example.realtimechat.service.impl.UserTokenServiceImpl.TokenType.REFRESH;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    @Value("${token.refresh.exp}")
    long refreshExp;
    ResponseCookie clearCookie = ResponseCookie
            .from(REFRESH.name(), "").sameSite(Cookie.SameSite.LAX.attributeValue())
            .httpOnly(true).path("/").secure(true)
            .maxAge(Duration.ofMinutes(0)).build();
    ;

    @PostMapping("sign-up")
    ResponseEntity<?> signUp(
            @RequestBody() @Validated({Default.class, GroupValidation.OnCreate.class})
            NguoiDung.NguoiDungDangKi nguoiDungDangKi) {
        if (authService.signUp(nguoiDungDangKi)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("sign-in")
    ResponseEntity<?> signIn(@RequestBody @Valid NguoiDungLogin nguoiDungLogin, HttpServletRequest httpRequest) {
        try {
            var res = authService.signIn(nguoiDungLogin);
            HttpHeaders httpHeaders = new HttpHeaders();
            ResponseCookie cookie = ResponseCookie
                    .from(REFRESH.name(), res.refreshToken()).sameSite(Cookie.SameSite.LAX.attributeValue())
                    .httpOnly(true).path("/").secure(true)
                    .maxAge(Duration.ofMinutes(this.refreshExp)).build();

            httpHeaders.set(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok().headers(httpHeaders).body(res);
        } catch (Exception e) {
            ProblemDetail p = getProblemDetail(HttpStatus.LOCKED, e.getMessage(), httpRequest, "Sign-in fail");
            return ResponseEntity.badRequest().body(p);
        }
    }

    @PatchMapping("sign-out")
    ResponseEntity<?> signOut(@NonNull HttpServletRequest httpRequest) {
        String token = getRefreshFromRq(httpRequest);
        ResponseEntity<ProblemDetail> p = getProblemDetailResponseEntityWithRefreshToken(httpRequest, token, "Đăng " +
                "xuất thất bại", "Sign-out Failed");
        if (p != null) return p;
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, clearCookie.toString()).build();
    }

    private @Nullable ResponseEntity<ProblemDetail> getProblemDetailResponseEntityWithRefreshToken(@NonNull HttpServletRequest httpRequest, String token, String detail, String title) {
        if (token == null || !authService.signOut(token)) {
            var p = getProblemDetail(HttpStatus.BAD_REQUEST, detail, httpRequest, title);
            return ResponseEntity.badRequest().body(p);
        }
        return null;
    }

    private @NonNull ProblemDetail getProblemDetail(HttpStatus status, String detail,
                                                    @NonNull HttpServletRequest httpRequest, String title) {
        var p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setInstance(URI.create(httpRequest.getRequestURI()));
        p.setTitle(title);
        p.setType(URI.create(httpRequest.getRequestURI()));
        p.setProperty("timestamp", new Date(System.currentTimeMillis()));
        return p;
    }

    private @Nullable String getRefreshFromRq(@NonNull HttpServletRequest httpRequest) {
        var cookies = httpRequest.getCookies();
        String token = null;
        if (cookies != null) {
            for (var item : cookies) {
                token = Objects.equals(item.getName(), REFRESH.name()) ? item.getValue() : null;
            }
        }
        return token;
    }

    @GetMapping("refresh-token")
    ResponseEntity<?> refreshToken(HttpServletRequest httpRequest) {
        var token = getRefreshFromRq(httpRequest);
        ResponseEntity<ProblemDetail> p = getProblemDetailResponseEntityWithRefreshToken(httpRequest, token, "Token " +
                "lỗi", "refresh-token");
        if (p != null) return p;
       try {
           return ResponseEntity.ok(Map.of("token", authService.createTokenFromRefreshToken(token)));
       }catch (JwtException e){
           return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
       }
    }
}
