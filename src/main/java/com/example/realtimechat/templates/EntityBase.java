package com.example.realtimechat.templates;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;


@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class EntityBase {
    @UuidGenerator
    @Id
    private UUID id;
    @CreationTimestamp
    @Column(updatable = false, name = "creation_time")
    private LocalDateTime creationTime;
    @UpdateTimestamp
    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;
}
