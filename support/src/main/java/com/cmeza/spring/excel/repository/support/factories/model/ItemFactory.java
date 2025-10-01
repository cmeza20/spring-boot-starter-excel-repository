package com.cmeza.spring.excel.repository.support.factories.model;


import com.cmeza.spring.excel.repository.support.members.ValueObject;

import java.util.Map;

public interface ItemFactory<T> {
    ItemFactory<T> withValue(String attribute, Object value);

    ItemFactory<T> withValue(String attribute, Object value, Class<?> castClass);

    Map<String, ValueObject> build();
}
