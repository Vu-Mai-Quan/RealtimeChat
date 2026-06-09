package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.model.Friend;
import com.example.realtimechat.db1.model.FriendRequest;
import com.example.realtimechat.db1.repositories.FriendRepository;
import com.example.realtimechat.db1.repositories.FriendRequestRepository;
import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.templates.identity.CompositeFriendId;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FriendServiceImpl implements com.example.realtimechat.service.FriendService {

    FriendRequestRepository friendRequestRepository;
    NguoiDungRepository nguoiDungRepository;
    FriendRepository friendRepository;

    @Override
    public boolean sendFriendRequest(FriendRequest.FriendRequestDTO rq) throws AccountNotFoundException {
        var currentUser =
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.equals(rq.toUsername())) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }
        var to = nguoiDungRepository.findByEmail(rq.toUsername()).orElseThrow(() -> new AccountNotFoundException(
                "User with email " + rq.toUsername() + " not found"));
        var from =
                nguoiDungRepository.findByEmail(currentUser).orElseThrow(() -> new AccountNotFoundException(
                        "Current user not found"));

        Example<Friend> exampleFriend =
                Example.of(new Friend(new CompositeFriendId(from.getId(), to.getId()),
                        null, null, null));

        if (friendRequestRepository.requestExists(from, to) || friendRepository.exists(exampleFriend)) {
            throw new IllegalStateException("Friend request already exists or you are already friends");
        }
        FriendRequest friendRequestFromTo = FriendRequest.builder()
                .compositeFriendId(new CompositeFriendId())
                .from(from)
                .isNewRecord(true)
                .message(rq.message())
                .to(to)
                .build();
        friendRequestRepository.save(friendRequestFromTo);
        return true;
    }
}
