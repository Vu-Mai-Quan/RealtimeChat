package com.example.realtimechat.db1.model;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Table(name = "tbl_conversation", indexes = {
        @Index(name = "idx_last_message_at", columnList = "last_message_at DESC"),
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
    @CollectionTable(name = "room_participant", joinColumns = @JoinColumn(name = "room_id"),
            indexes = {@Index(name = "idx_room_id", columnList = "nguoi_dung_id")})
    @OrderBy("nguoi_dung_id desc")
    List<Participant> participants;

    @Embedded
    Group group;

    @Column(name = "last_message_at")
    @OrderBy("asc")
    LocalDateTime lastMessageAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "conversation_seen_by",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "nguoi_dung_id"))
    List<NguoiDung> seenBy;

    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @OneToOne(fetch = FetchType.EAGER, cascade = {REMOVE, PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "last_message_id")
    LastMessage lastMessage;

    @CollectionTable(name = "unread_count", joinColumns = @JoinColumn(name = "conversation_id"))
    @MapKeyJoinColumn(name = "nguoi_dung_id")
    @Column(name = "unread_count")
    @ElementCollection
    Map<NguoiDung, Integer> unreadCount;

    public enum Type {
        DIRECT, GROUP
    }
}
