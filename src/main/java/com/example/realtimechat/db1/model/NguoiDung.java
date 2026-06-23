package com.example.realtimechat.db1.model;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.realtimechat.db1.repositories.NguoiDungRepository;
import com.example.realtimechat.templates.EntityBase;
import com.example.realtimechat.validations.GroupValidation;
import com.example.realtimechat.validations.ValueUniqueExist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "nguoi_dung")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class NguoiDung extends EntityBase implements UserDetails {

    /**
     *
     */
    @Serial private static final long serialVersionUID = 6126107163171470650L;

    @Column(nullable = false, unique = true, updatable = false, length = 100)
    @ColumnTransformer(write = "LOWER(?)")
    String email;

    String password;

    @Column(name = "display_name", nullable = false, length = 100)
//    @JdbcTypeCode(SqlTypes.NVARCHAR)
    String displayName;

    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "phone_number", unique = true, length = 13)
    String phoneNumber;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    String bio;

    @JdbcTypeCode(SqlTypes.JSON)
    @ColumnDefault("'[]'")
    Set<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles == null || roles.isEmpty() ? List.of() : roles.stream().map(SimpleGrantedAuthority::new).toList();
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        NguoiDung nguoiDung = (NguoiDung) o;
        return getId() != null && Objects.equals(getId(), nguoiDung.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
                getClass().hashCode();
    }

    /**
     * Lớp DTO đăng nhập
     *
     */
    @AllArgsConstructor
    @SuperBuilder
    @FieldDefaults(level = AccessLevel.PROTECTED)
    @Getter
    @NoArgsConstructor
    public static class NguoiDungLogin {
        @Email(message = "Định dạng email không hợp lệ")
        @ValueUniqueExist(repository = NguoiDungRepository.class,
                groups = {GroupValidation.OnCreate.class}, message = "Email đã tồn tại")
        String email;
        @NotBlank(message = "Mật khẩu không được để trống")
        String password;
    }


    /**
     * Lớp DTO đăng kí
     *
     */
    @SuperBuilder
    @FieldDefaults(level = AccessLevel.PROTECTED)
    @Getter
    @NoArgsConstructor
    public static class NguoiDungDangKi extends NguoiDungLogin {
        @NotBlank(message = "Họ không được để trống")
        String firstName;
        @NotBlank(message = "Tên không được để trống")
        String lastName;

        public NguoiDungDangKi(String email, String password, String firstName, String lastName) {
            super(email, password);
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getDisplayName() {
            return firstName.trim() + " " + lastName.trim();
        }
    }

    public record NguoiDungDisplay(String displayName, String avatarUrl, UUID id) {

    }
}
