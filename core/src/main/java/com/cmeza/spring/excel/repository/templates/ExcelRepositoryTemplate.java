package com.cmeza.spring.excel.repository.templates;

import com.cmeza.spring.excel.repository.ExcelRepositoryOperations;
import com.cmeza.spring.excel.repository.builders.ExcelToModelBuilder;
import com.cmeza.spring.excel.repository.builders.ExcelToModelMapBuilder;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;

public class ExcelRepositoryTemplate extends AbstractRepositoryTemplate implements ExcelRepositoryOperations {
    @Override
    public ModelToExcelBuilder toExcel() {
        return getDialect().toExcel().loggable(excelRepositoryProperties.isLoggable());
    }

    @Override
    public <T> ExcelToModelBuilder<T> toModel(Class<T> clazz) {
        return getDialect().toModel(clazz).loggable(excelRepositoryProperties.isLoggable());
    }

    @Override
    public <T, M> ExcelToModelMapBuilder<T, M> toModel(Class<T> clazz, Class<M> mapClass) {
        ExcelToModelMapBuilder<T, M> excelToModelMapBuilder = getDialect().toModel(clazz, mapClass);
        excelToModelMapBuilder.loggable(excelRepositoryProperties.isLoggable());
        return excelToModelMapBuilder;
    }
}
