package com.example.realtimechat.validations;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import com.example.realtimechat.db1.repositories.FieldExist;

import jakarta.el.MethodNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValueUniqueExistImpl implements ConstraintValidator<ValueUniqueExist, String> {
    private static final Logger log = LoggerFactory.getLogger(ValueUniqueExistImpl.class);
    private final ApplicationContext context;
    private FieldExist fieldExist;

    @Override
    public void initialize(@NonNull ValueUniqueExist constraintAnnotation) {
        try {
            fieldExist = context.getBean(constraintAnnotation.repository());
        } catch (BeansException e) {
            throw new IllegalStateException("Lấy bean không hợp lệ", e);
        } catch (MethodNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isValid(String value, @NonNull ConstraintValidatorContext context) {
        try {
            boolean e = fieldExist.fieldExistByValue(value);
            return !e;
        } catch (Exception e) {
            log.error("Lỗi validate: {}", e.getMessage());
            return false;
        }

    }


}
