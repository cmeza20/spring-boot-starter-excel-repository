package com.cmeza.spring.excel.repository.repositories.executors.types.impl;

import com.cmeza.spring.excel.repository.repositories.executors.types.ReturnType;
import com.cmeza.spring.excel.repository.builders.ExcelToModelBuilder;
import com.cmeza.spring.excel.repository.builders.ExcelToModelMapBuilder;
import com.cmeza.spring.excel.repository.support.exceptions.ExecuteUnsupportedException;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import com.cmeza.spring.excel.repository.utils.ModelUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public enum ExcelToModelMapReturnType implements ExecuteImplementation<ExcelToModelMapBuilder<Object, Object>> {
    LIST {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            return executeAndReturnListValue(builder, args);
        }
    },
    SET {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            List<Object> result = executeAndReturnListValue(builder, args);
            return new HashSet<>(result);
        }
    },
    STREAM {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            List<Object> result = executeAndReturnListValue(builder, args);
            return Stream.of(result);
        }
    },
    VALIDATED {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildValidated(arg);
        }
    },
    EXCEL_VALIDATED {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildExcelValidated(arg);
        }
    },
    VIEW_VALIDATED {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildViewValidated(arg);
        }
    },
    VALIDATED_ERROR {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildValidatedError(arg);
        }
    },
    EXCEL_VALIDATED_ERROR {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildExcelValidatedError(arg);
        }
    },
    VIEW_VALIDATED_ERROR {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildViewValidatedError(arg);
        }
    },
    MAP_VALIDATED {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildMapValidated(arg);
        }
    },
    MAP_EXCEL_VALIDATED {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildMapExcelValidated(arg);
        }
    },
    MAP_VIEW_VALIDATED {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildMapViewValidated(arg);
        }
    },
    MAP_VALIDATED_ERROR {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildMapValidatedError(arg);
        }
    },
    MAP_EXCEL_VALIDATED_ERROR {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildMapExcelValidatedError(arg);
        }
    },
    MAP_VIEW_VALIDATED_ERROR {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            File arg = ModelUtils.argsValidate(args);
            return builder.buildMapViewValidatedError(arg);
        }
    },
    UNSUPPORTED {
        @Override
        public Object execute(ExcelToModelMapBuilder<Object, Object> builder, Object... args) {
            throw new ExecuteUnsupportedException("Return type not supported");
        }
    };

    public static ExcelToModelMapReturnType from(ReturnType returnType) {
        return ExcelUtils.returnTypeFromEnumThrow(ExcelToModelMapReturnType.class, returnType);
    }

    private static List<Object> executeAndReturnListValue(ExcelToModelBuilder<Object> builder, Object... args) {
        File arg = ModelUtils.argsValidate(args);
        return builder.build(arg);
    }
}
