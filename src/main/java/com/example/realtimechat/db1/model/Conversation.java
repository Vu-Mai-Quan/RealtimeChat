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
import java.util.UUID;

@Table(name = "tbl_conversation")
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
    @Column(nullable = false)
    Type type;

    @ElementCollection
    @CollectionTable(name = "room_participant", joinColumns = @JoinColumn(name = "room_id"))
    List<Participant> participants;

    @Embedded
    Group group;

    @Column(name = "last_message_at")
    @CreationTimestamp
    LocalDateTime lastMessageAt;

    @ManyToOne()
    @JoinColumn(name = "seen_by")
    NguoiDung seenBy;

    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    public enum Type {
        DIRECT, GROUP
    }
}
