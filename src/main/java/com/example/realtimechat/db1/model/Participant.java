package com.example.realtimechat.db1.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Participant {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    NguoiDung nguoiDung;

    @Column(name = "join_at", nullable = false)
    @CreationTimestamp
    LocalDateTime joinAt;

}
