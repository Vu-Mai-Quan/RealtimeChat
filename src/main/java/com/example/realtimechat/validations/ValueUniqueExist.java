package com.example.realtimechat.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Kiểm tra giá trị độc nhất đã tồn tại trong cơ sở dữ liệu hay chưa.
 * Ví dụ: Kiểm tra email đã tồn tại hay chưa.
 *
 */
@Constraint(validatedBy = {ValueUniqueExistImpl.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ValueUniqueExist {
    String field();

    String message() default "Email ${validateValue} đã tồn tại";

    /**
     * Repository để kiểm tra giá trị đã tồn tại hay chưa.
     * Phải có phương thức boolean existsBy${field}(String value) để kiểm tra giá trị đã tồn tại hay chưa.
     *
     */
    Class<? extends JpaRepository<?, ?>> repository();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
