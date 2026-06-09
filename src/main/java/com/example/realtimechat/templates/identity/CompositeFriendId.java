package com.example.realtimechat.templates.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompositeFriendId implements Serializable {

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
