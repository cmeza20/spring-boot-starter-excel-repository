package com.cmeza.spring.excel.repository;

import com.cmeza.spring.excel.repository.builders.ExcelToModelBuilder;
import com.cmeza.spring.excel.repository.builders.ExcelToModelMapBuilder;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;

public interface ExcelRepositoryOperations {
    ModelToExcelBuilder toExcel();

    <T> ExcelToModelBuilder<T> toModel(Class<T> clazz);

    <T, M> ExcelToModelMapBuilder<T, M> toModel(Class<T> clazz, Class<M> mapClass);
}
