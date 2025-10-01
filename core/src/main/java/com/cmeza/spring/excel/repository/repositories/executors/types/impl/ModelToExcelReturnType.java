package com.cmeza.spring.excel.repository.repositories.executors.types.impl;

import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;
import com.cmeza.spring.excel.repository.support.exceptions.ExecuteUnsupportedException;
import com.cmeza.spring.excel.repository.repositories.executors.types.ReturnType;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;

import java.nio.file.Path;

public enum ModelToExcelReturnType implements ExecuteImplementation<ModelToExcelBuilder> {
    VIEW {
        @Override
        public Object execute(ModelToExcelBuilder builder, Object[] args) {
            return builder.buildView();
        }
    },
    PATH {
        @Override
        public Object execute(ModelToExcelBuilder builder, Object[] args) {
            return builder.buildFile();
        }
    },
    FILE {
        @Override
        public Object execute(ModelToExcelBuilder builder, Object[] args) {
            Path path = builder.buildFile();
            return path.toFile();
        }
    },
    UNSUPPORTED {
        @Override
        public Object execute(ModelToExcelBuilder builder, Object[] args) {
            throw new ExecuteUnsupportedException("Return type not supported");
        }
    };

    public static ModelToExcelReturnType from(ReturnType returnType) {
        return ExcelUtils.returnTypeFromEnumThrow(ModelToExcelReturnType.class, returnType);
    }
}
