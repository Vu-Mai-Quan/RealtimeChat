package com.example.realtimechat.db1.repositories;

import com.example.realtimechat.db1.model.FriendRequest;
import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.templates.identity.CompositeFriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, CompositeFriendId>{
    @Query("""
            select case count(fr)
                when 0 then false
                else true
            end
            from FriendRequest fr where (fr.from = ?1 and fr.to = ?2) or (fr.from= ?2 and fr.to = ?1)
            """)
    boolean requestExists(@NonNull NguoiDung from, @NonNull NguoiDung to);

}