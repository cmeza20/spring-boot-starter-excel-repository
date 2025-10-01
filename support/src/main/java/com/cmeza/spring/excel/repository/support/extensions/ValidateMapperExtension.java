package com.cmeza.spring.excel.repository.support.extensions;


public interface ValidateMapperExtension<T> {
    default void bindError(T entity, ItemErrorExtension<T> itemErrorExtension) {

    }
}
