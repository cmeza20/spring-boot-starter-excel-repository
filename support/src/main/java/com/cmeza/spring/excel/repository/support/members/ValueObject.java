package com.cmeza.spring.excel.repository.support.members;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValueObject {
    private final Object value;
    private final boolean hierarchical;
    private final Class<?> castClass;

    public ValueObject(Object value) {
        this.value = value;
        this.hierarchical = false;
        this.castClass = null;
    }
}
