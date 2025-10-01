package com.cmeza.spring.excel.repository.builders;

import com.cmeza.spring.excel.repository.support.configurations.model.AttributeConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.model.ModelConfiguration;
import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.results.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ExcelToModelBuilder<T> extends ExcelGenericBuilder<ExcelToModelBuilder<T>> {

    ExcelToModelBuilder<T> withModelConfiguration(ModelConfiguration<T> modelConfiguration);

    ExcelToModelBuilder<T> withModelConfiguration(Consumer<ModelConfiguration<T>> consumer);

    ExcelToModelBuilder<T> withAttributeConfiguration(Consumer<AttributeConfiguration> consumer);

    ExcelToModelBuilder<T> withModelMapper(ToModelMapper<T> mapper);

    ExcelToModelBuilder<T> withSheetIndex(int sheetIndex);

    ExcelToModelBuilder<T> withSheetName(String sheetName);

    ExcelToModelBuilder<T> withRowCacheSize(int keepRowMemory);

    ExcelToModelBuilder<T> withBufferSize(int bufferSize);

    <A extends Annotation> ExcelToModelBuilder<T> withModelAnnotation(Class<A> clazz, BiConsumer<A, ModelConfiguration<T>> consumer);

    <A extends Annotation> ExcelToModelBuilder<T> withAttributeAnnotation(Class<A> clazz, BiConsumer<A, AttributeConfiguration> consumer);

    ExcelToModelBuilder<T> withMapping(String fieldName);

    ExcelToModelBuilder<T> withMapping(String fieldName, String headerName);

    ExcelToModelBuilder<T> withMapping(String fieldName, String headerName, Class<?> fieldType);

    ExcelToModelBuilder<T> withMapping(AttributeConfiguration attributeConfiguration);

    List<T> build(File excelFile);

    Validated<T> buildValidated(File excelFile);

    ExcelValidated<T> buildExcelValidated(File excelFile);

    ValidatedError<T> buildValidatedError(File excelFile);

    ExcelValidatedError<T> buildExcelValidatedError(File excelFile);

    ViewValidated<T> buildViewValidated(File excelFile);

    ViewValidatedError<T> buildViewValidatedError(File excelFile);

    ExcelToModelBuilder<T> withErrorFolder(Path folder);

    ExcelToModelBuilder<T> withErrorFileName(String fileName);

    ExcelToModelBuilder<T> withErrorVersioned(boolean versioned);

}
