package com.example.realtimechat.db1.repositories;

public interface FieldExist {
    default boolean fieldExistByValue(String value) {
        throw new RuntimeException("chưa ghi đè phương thức này");
    }
}
