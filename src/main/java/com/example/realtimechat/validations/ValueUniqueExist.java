package com.example.realtimechat.validations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.example.realtimechat.db1.repositories.FieldExist;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Kiểm tra giá trị độc nhất đã tồn tại trong cơ sở dữ liệu hay chưa.
 * Ví dụ: Kiểm tra email đã tồn tại hay chưa.
 *
 */
@Constraint(validatedBy = {ValueUniqueExistImpl.class})
@Target({METHOD, FIELD, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface ValueUniqueExist {

    String message() default "Giá trị đã tồn tại";

    Class<? extends FieldExist> repository();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
