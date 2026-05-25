package com.example.realtimechat.db1.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Table(name = "tbl_conversation", indexes = {
        @Index(name = "idx_last_message_at", columnList = "last_message_at DESC"),
        @Index(name = "idx_create_at", columnList = "create_at DESC")
})
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    Type type;

    @ElementCollection
    @CollectionTable(name = "room_participant", joinColumns = @JoinColumn(name = "room_id"))
    List<Participant> participants;

    @Embedded
    Group group;

    @Column(name = "last_message_at")
    @CreationTimestamp
    LocalDateTime lastMessageAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "conversation_seen_by",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "nguoi_dung_id"))
    List<NguoiDung> seenBy;

    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    LastMessage lastMessage;

    @CollectionTable(name = "unread_count", joinColumns = @JoinColumn(name = "conversation_id"))
    @MapKeyJoinColumn(name = "nguoi_dung_id")
    @Column(name = "unread_count")
    Map<NguoiDung, Integer> unreadCount;

    public enum Type {
        DIRECT, GROUP
    }
}
