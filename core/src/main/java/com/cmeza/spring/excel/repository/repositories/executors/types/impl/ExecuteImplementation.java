package com.cmeza.spring.excel.repository.repositories.executors.types.impl;

import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;

public interface ExecuteImplementation<T extends ExcelGenericBuilder<?>> {
    Object execute(T builder, Object... args);
}
