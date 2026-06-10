package com.example.realtimechat.db1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.realtimechat.db1.model.Friend;
import com.example.realtimechat.templates.identity.CompositeFriendId;

public interface FriendRepository extends JpaRepository<Friend, CompositeFriendId> {
}