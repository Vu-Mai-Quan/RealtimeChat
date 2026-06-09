package com.example.realtimechat.service;

import com.example.realtimechat.db1.model.FriendRequest;

import javax.security.auth.login.AccountNotFoundException;

public interface FriendService {
    boolean sendFriendRequest(FriendRequest.FriendRequestDTO rq) throws AccountNotFoundException;
}
