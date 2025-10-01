package com.cmeza.spring.excel.repository.mappers;

import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;

public class DefaultToModelMapper<T> implements ToModelMapper<T> {

    private final Class<T> clazz;

    public DefaultToModelMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<T> getClazz() {
        return clazz;
    }
}
