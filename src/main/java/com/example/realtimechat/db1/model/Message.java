package com.example.realtimechat.db1.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Table(name = "tbl_message", indexes = {
        @Index(name = "idx_conversation_and_create_at", unique = true,
                columnList = "conversation_id ASC, create_at DESC"),

})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    Conversation conversation;

    @ManyToOne()
    @JoinColumn(nullable = false, name = "sender_id")
    NguoiDung nguoiDung;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    String content;

    @Column(name = "image_url")
    String imageUrl;

    @Column(nullable = false, name = "create_at")
    @CreationTimestamp
    LocalDateTime createAt;

    @PrePersist
    void trimContent() {
        this.content = this.content.trim();
    }
}
