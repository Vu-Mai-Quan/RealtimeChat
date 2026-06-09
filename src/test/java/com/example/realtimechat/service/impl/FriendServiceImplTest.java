package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.model.FriendRequest;
import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.service.FriendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion.$2Y;

//@SpringBootTest(properties = {
//        "spring.datasource.url=jdbc:postgresql://db.njclasmglmchfoaujckz.supabase.co:5432/postgres",
//        "spring.datasource.driverClassName=org.h2.Driver",
//        "spring.datasource.username=postgres",
//        "spring.datasource.password=vumaiquan@Gmail",
//        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
//        "spring.jpa.hibernate.ddl-auto=none",
//        "spring.jpa.show-sql=true",
//},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:sqlite:src/main/resources/database/test.sqlite",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.driver-class-name=org.sqlite.JDBC",
        "spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect",
        "spring.jpa.show-sql=true",
})
@Import(FriendServiceImpl.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FriendServiceImplTest {

    @Autowired
    FriendService friendService;

    @Autowired
    NguoiDungRepository nguoiDungRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder($2Y);

    @Test
    void sendFriendRequest() throws AccountNotFoundException {
        NguoiDung nguoiDung = NguoiDung.builder()
                .email("vumaiquan28062002@gmail.com")
                .displayName("vumaiquan28062002")
                .password(passwordEncoder.encode("test"))
                .build(), to = NguoiDung.builder()
                .email("vumaiquan280602@gmail.com")
                .displayName("vumaiquan280602")
                .password(passwordEncoder.encode("test"))
                .build();
        nguoiDungRepository.saveAll(List.of(nguoiDung, to));


        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "vumaiquan28062002@gmail.com", "test"));
//        FriendRequest.FriendRequestDTO rq = new FriendRequest.FriendRequestDTO("vumaiquan28062002@gmail.com", ""),
//                rq1 = new FriendRequest.FriendRequestDTO("vumaiquan280620@gmail.com", "");
//        assertThrows(IllegalArgumentException.class, () -> friendService.sendFriendRequest(rq));
//        assertThrows(AccountNotFoundException.class, () -> friendService.sendFriendRequest(rq1));
        assertTrue(friendService.sendFriendRequest(new FriendRequest.FriendRequestDTO("vumaiquan280602@gmail.com",
                "")));
        assertThrows(IllegalStateException.class,
                () -> friendService.sendFriendRequest(new FriendRequest.FriendRequestDTO("vumaiquan280602@gmail.com",
                        "")));
    }
}