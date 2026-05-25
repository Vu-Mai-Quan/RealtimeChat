package com.example.realtimechat.db1.repositories;

import com.example.realtimechat.db1.model.NguoiDung;
import com.example.realtimechat.db1.model.NguoiDung_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID>, JpaSpecificationExecutor<NguoiDung> {
    boolean existsByEmail(@NonNull String email);

    Optional<NguoiDung> findByEmail(@NonNull String email);

    @Query(value = "update NguoiDung nd set nd.tokenUserKey = ?2 where nd.id = ?1")
    @Modifying
    @Transactional
    void updateVersionToken(UUID id, UUID newValue);

    default Specification<NguoiDung> findLessThan() {

        return (root, query, cb) -> {
            assert query != null;
            query.groupBy(root.get(NguoiDung_.email)).having(cb.lessThan(root.get(NguoiDung_.creationTime), LocalDateTime.now()));
            return cb.and(cb.lessThan(root.get(NguoiDung_.email), ""), cb.lessThan(root.get(NguoiDung_.email), ""));
        };
    }
}