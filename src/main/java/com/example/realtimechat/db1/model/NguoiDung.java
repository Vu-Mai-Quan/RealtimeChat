package com.example.realtimechat.db1.model;

import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.templates.EntityBase;
import com.example.realtimechat.validations.ValueUniqueExist;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "nguoi_dung")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class NguoiDung extends EntityBase implements UserDetails {

    @Column(nullable = false, unique = true, updatable = false, length = 100)
    @ColumnTransformer(write = "LOWER(?)")
    String email;

    String password;

    @Column(name = "display_name", nullable = false, length = 100)
//    @JdbcTypeCode(SqlTypes.NVARCHAR)
    String displayName;

    //    @JdbcTypeCode(value = SqlTypes.JSON)
//    Map<String, Object> information;
    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "phone_number", unique = true, length = 13)
    String phoneNumber;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    String bio;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Column(name = "token_user_key", unique = true)
    public UUID tokenUserKey;


    @PrePersist
    @PreUpdate
    void onCreateUpdate() {
        if (email != null) {
            email = email.trim();
        } else if (displayName != null) {
            displayName = displayName.trim();
        }
        tokenUserKey = UUID.randomUUID();
    }

    /**
     * Lớp DTO đăng nhập
     *
     */
    @AllArgsConstructor
    @SuperBuilder
    @FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
    @Getter
    public static class NguoiDungLogin {
        @Email(message = "Định dạng email không hợp lệ")

        @ValueUniqueExist(field = "email", repository = NguoiDungRepository.class)
        String email;
        @NotBlank(message = "Mật khẩu không được để trống")
        String password;
    }

    /**
     * Lớp DTO đăng kí
     *
     */
    @SuperBuilder
    @FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
    @Getter
    public static class NguoiDungDangKi extends NguoiDungLogin {
        @NotBlank(message = "Họ không được để trống")
        String fistName;
        @NotBlank(message = "Tên không được để trống")
        String lastName;

        public NguoiDungDangKi(String email, String password, String fistName, String lastName) {
            super(email, password);
            this.fistName = fistName;
            this.lastName = lastName;
        }

        public String getDisplayName() {
            return fistName.trim() + " " + lastName.trim();
        }
    }
}
