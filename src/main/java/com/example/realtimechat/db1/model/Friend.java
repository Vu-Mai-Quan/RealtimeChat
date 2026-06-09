package com.example.realtimechat.db1.model;

import com.example.realtimechat.templates.identity.CompositeFriendId;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "tbl_friend", indexes = {
        @Index(name = "uk_user_friend", columnList = "user_id ASC, friend_id ASC", unique = true),})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Friend {
    @EmbeddedId
    @Setter(AccessLevel.NONE)
    CompositeFriendId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userA")
    @JoinColumn(name = "user_a")
    NguoiDung userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userB")
    @JoinColumn(name = "user_b")
    NguoiDung userB;

    @CreationTimestamp
    LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void sortUser() {
        if (userA.getId().compareTo(userB.getId()) > 0) {
            NguoiDung temp = userA;
            userA = userB;
            userB = temp;
        }
    }
}
