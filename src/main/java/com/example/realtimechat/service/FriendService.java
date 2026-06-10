package com.example.realtimechat.service;

import javax.security.auth.login.AccountNotFoundException;

import com.example.realtimechat.db1.model.FriendRequest;

public interface FriendService {
    boolean sendFriendRequest(FriendRequest.FriendRequestDTO rq) throws AccountNotFoundException;
}
