package com.cmeza.spring.excel.repository.support.builders;

public interface ExcelGenericBuilder<T> {

    T withKey(String key);

    T loggable(boolean loggable);

    T withParameter(String param, Class<?> clazz);
    
    T withParameter(String param, Object value);
}
