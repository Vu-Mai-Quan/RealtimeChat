package com.example.realtimechat.db1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LastMessage {
    @Column(name = "id_message")
    UUID idMessage;
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    String content;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    NguoiDung sender;
}
