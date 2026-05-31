package com.example.realtimechat.db1.model;

import com.example.realtimechat.templates.identity.CompositeFriendId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "tbl_friend_request", indexes = {
        @Index(name = "idx_from_to", columnList = "from_user_id ASC, to_user_id ASC", unique = true),
        @Index(name = "idx_from_user", columnList = "from_user_id ASC"),
        @Index(name = "idx_to_user", columnList = "to_user_id ASC"),
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class FriendRequest {
    @EmbeddedId
    CompositeFriendId compositeFriendId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_user_id")
    @MapsId("userA")
    NguoiDung from;

    @ManyToOne(optional = false)
    @MapsId("userB")
    @JoinColumn(name = "to_user_id")
    NguoiDung to;
    @Column(length = 300)
    String message;

    @CreationTimestamp
    LocalDateTime timestamp;
}
