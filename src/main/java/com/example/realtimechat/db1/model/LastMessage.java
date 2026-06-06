package com.example.realtimechat.db1.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "tbl_last_message", indexes = {@Index(name = "idx_lm_id_message",columnList = "id_message, create_at DESC")})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class LastMessage {
    @Id
    @Column(name = "id_message")
    UUID idMessage;
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    String content;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    NguoiDung sender;
    @Column(name = "create_at")
    @CreationTimestamp
    LocalDateTime createAt;
}
