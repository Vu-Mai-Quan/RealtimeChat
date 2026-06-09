package com.example.realtimechat.db1.repositories;

import com.example.realtimechat.db1.model.Friend;
import com.example.realtimechat.templates.identity.CompositeFriendId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, CompositeFriendId> {
}