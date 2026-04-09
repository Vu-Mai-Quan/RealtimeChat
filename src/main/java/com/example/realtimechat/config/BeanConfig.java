package com.example.realtimechat.config;

import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class BeanConfig {

    @Bean
    UserDetailsService userDetailsService(NguoiDungRepository nguoiDungRepository) {
        return username -> nguoiDungRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(
                "Thông tin tài khoản hoặc mật khẩu không chính xác"));
    }
}
