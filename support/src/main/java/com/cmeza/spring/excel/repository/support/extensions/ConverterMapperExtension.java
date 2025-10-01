package com.cmeza.spring.excel.repository.support.extensions;

import com.cmeza.spring.excel.repository.support.configurations.model.AttributeConfiguration;
import org.apache.poi.ss.usermodel.Cell;

public interface ConverterMapperExtension {
    default Object mapConverterValue(Cell cell, Object convertValue, AttributeConfiguration attributeConfiguration) {
        return convertValue;
    }
}
