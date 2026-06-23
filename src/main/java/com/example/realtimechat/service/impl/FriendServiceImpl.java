package com.example.realtimechat.service.impl;

import com.example.realtimechat.db1.model.NguoiDung;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Example;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.realtimechat.db1.model.Friend;
import com.example.realtimechat.db1.model.FriendRequest;
import com.example.realtimechat.db1.repositories.FriendRepository;
import com.example.realtimechat.db1.repositories.FriendRequestRepository;
import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.service.FriendService;
import com.example.realtimechat.templates.identity.CompositeFriendId;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final String CLASSNAME = this.getClass().getName();
    FriendRequestRepository friendRequestRepository;
    NguoiDungRepository nguoiDungRepository;
    FriendRepository friendRepository;


    @Override
    @Transactional
    public boolean sendFriendRequest(FriendRequest.@NonNull FriendRequestDTO rq) {
        var currentUser = (String) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (currentUser.equals(rq.toUsername())) {
            throw new IllegalArgumentException(CLASSNAME + ": Cannot send friend request to yourself");
        }

        var lsFromTo = nguoiDungRepository.findParticipants(currentUser, rq.toUsername());

        if (lsFromTo.size() != 2) {
            throw new UsernameNotFoundException(CLASSNAME +
                    "User with email " + rq.toUsername() + " not found");
        }
        lsFromTo.stream()
                .filter(nguoiDung -> nguoiDung.getEmail().equals(rq.toUsername()))
                .findFirst().orElseThrow(() -> new UsernameNotFoundException(CLASSNAME +
                        ": User with email " + rq.toUsername() + " not found"));
        var to = lsFromTo.get(1);
        var from = lsFromTo.get(0);
        Example<Friend> exampleFriend = Example.of(new Friend(
                new CompositeFriendId(from.getId(), to.getId()), null, null, null, true));

        if (friendRequestRepository.requestExists(from, to)
                || friendRepository.exists(exampleFriend)) {
            throw new IllegalStateException(CLASSNAME
                    + ": Friend request already exists or you are already friends");
        }
        FriendRequest friendRequestFromTo = FriendRequest.builder()
                .compositeFriendId(new CompositeFriendId()).from(from).isNewRecord(true)
                .message(rq.message()).to(to).build();
        friendRequestRepository.save(friendRequestFromTo);
        return true;
    }

    @Override
    @Transactional
    public NguoiDung.NguoiDungDisplay acceptFriendRequest(CompositeFriendId id) {
        var fr = getFriendRequest(id);
        var friend = new Friend(new CompositeFriendId(), fr.getFrom(), fr.getTo(), null, true);
        friendRepository.save(friend);
        friendRequestRepository.delete(fr);
        return new NguoiDung.NguoiDungDisplay(fr.getFrom().getDisplayName(), fr.getFrom().getAvatarUrl(),
                fr.getFrom().getId());
    }

    private @NonNull FriendRequest getFriendRequest(CompositeFriendId id) {
        var friendRequest = friendRequestRepository.findById(id);
        if (friendRequest.isEmpty()) {
            throw new UsernameNotFoundException(CLASSNAME + ": Friend request not found");
        } else if (!friendRequest.get().getTo().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            throw new IllegalArgumentException(CLASSNAME + ": You are not authorized to accept this friend request");
        }
   
        return friendRequest.get();
    }

    @Override
    @Transactional
    public boolean declineFriendRequest(CompositeFriendId id) {
        var fr = getFriendRequest(id);
        friendRequestRepository.delete(fr);
        return true;
    }
}
