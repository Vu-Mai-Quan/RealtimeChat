package com.example.realtimechat.db1.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import com.example.realtimechat.db1.model.NguoiDung;

public interface NguoiDungRepository extends FieldExist, JpaRepository<NguoiDung, UUID> {
    boolean existsByEmail(@NonNull String email);

    Optional<NguoiDung> findByEmail(@NonNull String email);

    @Query(value = "update NguoiDung nd set nd.tokenUserKey = ?2 where nd.id = ?1")
    @Modifying
    @Transactional
    void updateVersionToken(UUID id, UUID newValue);

    @Query("SELECT u FROM NguoiDung u WHERE u.email IN (?1, ?2) " +
            "ORDER BY CASE WHEN u.email = ?1 THEN 0 ELSE 1 END ASC")
    List<NguoiDung> findParticipants(
            String currentUser,
            String toUser
    );


    @Override
    default boolean fieldExistByValue(String value) {
        return existsByEmail(value);
    }
}