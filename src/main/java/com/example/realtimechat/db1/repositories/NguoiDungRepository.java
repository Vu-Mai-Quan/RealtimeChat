package com.example.realtimechat.db1.repositories;

import com.example.realtimechat.db1.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {
    boolean existsByEmail(@NonNull String email);

    Optional<NguoiDung> findByEmail(@NonNull String email);

}