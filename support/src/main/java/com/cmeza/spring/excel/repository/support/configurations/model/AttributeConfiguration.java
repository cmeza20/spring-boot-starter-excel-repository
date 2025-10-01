package com.cmeza.spring.excel.repository.support.configurations.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttributeConfiguration {
    private String headerName;
    private String fieldName;
    private boolean ignored;
    private Integer col;
    private Class<?> fieldType;
    private boolean reflectionMapping;

    public AttributeConfiguration(String fieldName) {
        this.fieldName = fieldName;
    }
}
