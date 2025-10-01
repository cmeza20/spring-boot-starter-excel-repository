package com.cmeza.spring.excel.repository.converters;

import com.cmeza.spring.excel.repository.converters.excel.ToExcelConverterImpl;
import com.cmeza.spring.excel.repository.converters.model.ToModelConverterImpl;
import com.cmeza.spring.excel.repository.support.converters.excel.ToExcelConverter;
import com.cmeza.spring.excel.repository.support.converters.model.ToModelConverter;
import com.cmeza.spring.excel.repository.support.converters.model.ToModelMapConverter;

public interface Converter {
    static ToExcelConverter toExcel() {
        return new ToExcelConverterImpl();
    }

    static <T> ToModelConverter<T> toModel(Class<T> modelClass) {
        return new ToModelConverterImpl<>(modelClass);
    }

    static <T, M> ToModelMapConverter<T, M> toModel(Class<T> modelClass, Class<M> mapClass) {
        return new ToModelConverterImpl<>(modelClass, mapClass);
    }
}
