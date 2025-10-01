package com.cmeza.spring.excel.repository.repositories.configurations;

import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.repositories.definitions.ParameterDefinition;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public class SimpleExcelConfigurationImpl implements SimpleExcelConfiguration {
    private final ExcelRepositoryTemplate excelTemplate;
    private final ParameterDefinition[] parameters;
    private final TypeMetadata typeMetadata;
    private final Class<?> targetClass;
    private final String configKey;
    private final boolean loggable;
    private final boolean needRowMapper;
    private final ExcelGenericBuilder<?> builder;
}
