package com.example.realtimechat.rest;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.realtimechat.db1.model.FriendRequest;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/friend/")
public class FriendController {

    private static final Logger log = LoggerFactory.getLogger(FriendController.class);

    @PostMapping
    ResponseEntity<?> addFriend(HttpServletRequest request,@RequestBody FriendRequest.FriendRequestDTO dto) {
        try {
            return ResponseEntity.ok("");
        } catch (RuntimeException e) {
            var p = AuthController.getProblemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), request, "Lỗi gửi yêu cầu kết bạn");
            log.error("Lỗi gửi lời mời kết bạn", e);
            return ResponseEntity.of(p).build();
        }
    }

    @PostMapping(":id/accept")
    ResponseEntity<?> acceptFriend(HttpServletRequest request, @PathVariable("id")UUID id) {
        try {
            return ResponseEntity.ok("");
        } catch (RuntimeException e) {
            var p = AuthController.getProblemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), request, "Lỗi đồng ý yêu cầu kết bạn");
            log.error("Lỗi đồng ý lời mời kết bạn", e);
            return ResponseEntity.of(p).build();
        }
    }

    @DeleteMapping(":id/decline")
    ResponseEntity<?> declineFriendRequest(HttpServletRequest request,@PathVariable UUID id) {
        try {
            return ResponseEntity.ok("");
        } catch (RuntimeException e) {
            var p = AuthController.getProblemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), request, "Lỗi từ chối yêu cầu kết bạn");
            log.error("Lỗi từ chối lời mời kết bạn", e);
            return ResponseEntity.of(p).build();
        }
    }

    @GetMapping("get")
    ResponseEntity<?> getAllFriend(HttpServletRequest request) {
        try {
            return ResponseEntity.ok("");
        } catch (RuntimeException e) {
            var p = AuthController.getProblemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), request, "Lỗi danh sách bạn");
            log.error("Lỗi danh sách bạn", e);
            return ResponseEntity.of(p).build();
        }
    }
    @GetMapping("requests")
    ResponseEntity<?> getAllFriendRequest(HttpServletRequest request) {
        try {
            return ResponseEntity.ok("");
        } catch (RuntimeException e) {
            var p = AuthController.getProblemDetail(HttpStatus.BAD_REQUEST, e.getMessage(), request, "Lỗi danh sách yêu cầu bạn");
            log.error("Lỗi danh sách yêu cầu bạn", e);
            return ResponseEntity.of(p).build();
        }
    }
}
