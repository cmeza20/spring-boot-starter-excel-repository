package com.cmeza.spring.excel.repository.repositories.executors;

import com.cmeza.spring.excel.repository.repositories.configurations.SimpleExcelConfiguration;
import com.cmeza.spring.excel.repository.repositories.executors.types.ExecutorType;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;

public interface ExcelExecutor {
    ExecutorType getExecuteType();

    Object execute(MethodMetadata methodMetadata, Object[] arguments);

    void attachConfiguration(SimpleExcelConfiguration simpleExcelConfiguration);

    void validateConfiguration(SimpleExcelConfiguration simpleExcelConfiguration);

    void print();
}
