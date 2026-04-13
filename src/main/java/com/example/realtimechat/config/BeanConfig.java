package com.example.realtimechat.config;

import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import jakarta.validation.ValidatorFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class BeanConfig {

    @Bean
    UserDetailsService userDetailsService(NguoiDungRepository nguoiDungRepository) {
        return username -> nguoiDungRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(
                "Thông tin tài khoản hoặc mật khẩu không chính xác"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y);
    }

    @Bean
    UserCache userCache() {
        return new SpringCacheBasedUserCache(new ConcurrentMapCache("users"));
    }


    @Bean
    AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder,
                                                  UserDetailsService userDetailsService, UserCache userCache) {
        var dao = new DaoAuthenticationProvider();
        dao.setPasswordEncoder(passwordEncoder);
        dao.setUserDetailsService(userDetailsService);
        dao.setUserCache(userCache);
        return dao;
    }
    //@NonNull HttpSecurity security, AuthenticationProvider
    //    authenticationProvider,
    @Bean
    AuthenticationManager authenticationManager(@lombok.NonNull HttpSecurity security, AuthenticationProvider
            authenticationProvider) throws Exception {
        var man = security.getSharedObject(AuthenticationManagerBuilder.class);
        man.authenticationProvider(authenticationProvider);

        return  man.build();
    }
}
