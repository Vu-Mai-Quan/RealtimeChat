package com.example.realtimechat.db1.repositories;

import jakarta.el.MethodNotFoundException;


public interface FieldExist {
    default boolean fieldExistByValue(String value) {

        throw new MethodNotFoundException("chưa ghi đè phương thức này");
    }
}
