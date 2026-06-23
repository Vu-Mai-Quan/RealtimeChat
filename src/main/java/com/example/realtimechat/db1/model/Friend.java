package com.example.realtimechat.db1.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import com.example.realtimechat.templates.identity.CompositeFriendId;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Persistable;

@Table(
	name = "tbl_friend",
	indexes = { @Index(
		name = "uk_user_friend", columnList = "user_id ASC, friend_id ASC",
		unique = true), })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Friend implements Persistable<CompositeFriendId> {
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
	@Transient
	private boolean isNewRecord = true;

	@PrePersist
	@PreUpdate
	public void sortUser() {
		if (userA.getId().compareTo(userB.getId()) > 0) {
			NguoiDung temp = userA;
			userA = userB;
			userB = temp;
		}
	}

	@Override
	public boolean isNew() {
		return this.isNewRecord;
	}

	@PostLoad
	private void load() {
		this.isNewRecord = false;
	}
}
