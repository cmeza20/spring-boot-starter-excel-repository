package com.cmeza.spring.excel.repository.dialects;

import com.cmeza.spring.excel.repository.ExcelRepositoryOperations;
import com.cmeza.spring.excel.repository.builders.ExcelToModelBuilder;
import com.cmeza.spring.excel.repository.builders.ExcelToModelMapBuilder;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;
import com.cmeza.spring.excel.repository.builders.abstracts.AbstractBuilderExcel;
import com.cmeza.spring.excel.repository.builders.impl.ExcelToModelBuilderImpl;
import com.cmeza.spring.excel.repository.builders.impl.ModelToExcelBuilderImpl;

public class DefaultDialect implements ExcelRepositoryOperations {

    protected final AbstractBuilderExcel.Impl impl;

    public DefaultDialect(AbstractBuilderExcel.Impl impl) {
        this.impl = impl;
    }

    @Override
    public ModelToExcelBuilder toExcel() {
        return new ModelToExcelBuilderImpl(impl);
    }

    @Override
    public <T> ExcelToModelBuilder<T> toModel(Class<T> clazz) {
        return new ExcelToModelBuilderImpl<>(impl, clazz);
    }

    @Override
    public <T, M> ExcelToModelMapBuilder<T, M> toModel(Class<T> clazz, Class<M> mapClass) {
        return new ExcelToModelBuilderImpl<>(impl, clazz, mapClass);
    }

}
