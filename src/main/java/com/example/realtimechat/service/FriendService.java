package com.example.realtimechat.service;

import com.example.realtimechat.db1.model.FriendRequest;
import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.templates.identity.CompositeFriendId;

public interface FriendService {
    boolean sendFriendRequest(FriendRequest.FriendRequestDTO rq);

    NguoiDung.NguoiDungDisplay acceptFriendRequest(CompositeFriendId id);

    boolean declineFriendRequest(CompositeFriendId id);
}
