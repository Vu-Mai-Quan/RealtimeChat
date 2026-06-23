package com.example.realtimechat.templates.identity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompositeFriendId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1128160230307559370L;

	UUID userA;

    UUID userB;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompositeFriendId that)) return false;
        return Objects.equals(userA, that.userA) && Objects.equals(userB, that.userB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userA, userB);
    }
 
}
