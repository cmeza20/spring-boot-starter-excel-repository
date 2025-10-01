package com.cmeza.spring.excel.repository.support.converters.model;

import com.cmeza.spring.excel.repository.support.configurations.model.AttributeConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.model.ModelConfiguration;
import com.cmeza.spring.excel.repository.support.extensions.ValidatedExtension;
import com.cmeza.spring.excel.repository.support.factories.model.ModelFactory;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ToModelConverter<T> {

    ToModelConverter<T> withModelConfiguration(ModelConfiguration<T> modelConfiguration);

    ToModelConverter<T> withModelConfiguration(Consumer<ModelConfiguration<T>> consumer);

    ToModelConverter<T> withAttributeConfiguration(Consumer<AttributeConfiguration> consumer);

    <A extends Annotation> ToModelConverter<T> withModelAnnotation(Class<A> clazz, BiConsumer<A, ModelConfiguration<T>> consumer);

    <A extends Annotation> ToModelConverter<T> withAttributeAnnotation(Class<A> clazz, BiConsumer<A, AttributeConfiguration> consumer);

    ToModelConverter<T> withModelMapper(ToModelMapper<T> mapper);

    ToModelConverter<T> withSheetIndex(int sheetIndex);

    ToModelConverter<T> withSheetName(String sheetName);

    ToModelConverter<T> withRowCacheSize(int keepRowMemory);

    ToModelConverter<T> withBufferSize(int bufferSize);

    ToModelConverter<T> withMapping(String fieldName);

    ToModelConverter<T> withMapping(String fieldName, String headerName);

    ToModelConverter<T> withMapping(String fieldName, String headerName, Class<?> fieldType);

    ToModelConverter<T> withMapping(AttributeConfiguration attributeConfiguration);

    List<T> build(File excelFile);

    void preBuild(ModelFactory<T> modelFactory);

    ModelConfiguration<T> getConfiguration();

    ValidatedExtension<T> toValidated();

}
