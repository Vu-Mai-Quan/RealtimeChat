package com.example.realtimechat.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.StringUtils;


public class ValueUniqueExistImpl implements ConstraintValidator<ValueUniqueExist, String> {
    private final ApplicationContext applicationContext;
    private JpaRepository<?, ?> repository;
    private String field;

    public ValueUniqueExistImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(@NonNull ValueUniqueExist constraintAnnotation) {
        field = constraintAnnotation.field();
        repository = applicationContext.getBean(constraintAnnotation.repository());
    }

    @Override
    public boolean isValid(String value, @NonNull ConstraintValidatorContext context) {
        String fieldName = context.getDefaultConstraintMessageTemplate()
                .replace("{validateValue}", value);
        final String methodName = "existsBy" + StringUtils.capitalize(field);
        try {
            boolean exists = (boolean) repository.getClass()
                    .getMethod(methodName, String.class)
                    .invoke(repository, value.toLowerCase());
            if (exists) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(fieldName + " đã tồn tại")
                        .addConstraintViolation();
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra giá trị độc nhất: " + e.getMessage(), e);
        }
        return true;
    }


}
