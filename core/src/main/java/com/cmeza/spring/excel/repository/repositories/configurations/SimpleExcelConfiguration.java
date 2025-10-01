package com.cmeza.spring.excel.repository.repositories.configurations;

import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.repositories.definitions.ParameterDefinition;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;

public interface SimpleExcelConfiguration {
    String getConfigKey();

    ExcelRepositoryTemplate getExcelTemplate();

    ParameterDefinition[] getParameters();

    TypeMetadata getTypeMetadata();

    Class<?> getTargetClass();

    boolean isLoggable();

    <T extends ExcelGenericBuilder<?>> ExcelGenericBuilder<T> getBuilder();
}
