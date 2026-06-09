package com.example.realtimechat.db1.model;

import com.example.realtimechat.templates.identity.CompositeFriendId;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

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
@Builder
public class FriendRequest implements Persistable<CompositeFriendId> {
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

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean isNewRecord = true;

    @CreationTimestamp
    LocalDateTime timestamp;

    @Override
    public CompositeFriendId getId() {
        return this.compositeFriendId;
    }
    @Override
    public boolean isNew() {
        return isNewRecord;
    }


    public record FriendRequestDTO(String toUsername, String message) {
    }
}
