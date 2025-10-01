package com.cmeza.spring.excel.repository.support.converters.excel;

import com.cmeza.spring.excel.repository.support.configurations.excel.*;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ExcelSheetConverter extends ParentConverter {
    ExcelSheetConverter withHeader(Consumer<HeaderConfiguration> consumer);

    ExcelSheetConverter withTable(Consumer<TableConfiguration> consumer);

    ExcelSheetConverter withAutoSize(boolean autoSize);

    ExcelSheetConverter withSheetName(String sheetName);

    <E> ExcelSheetConverter withData(Collection<E> data);

    ExcelSheetConverter preBuilt(Class<?> clazz);

    ExcelSheetConverter withSheetConfiguration(Consumer<SheetConfiguration> consumer);

    ExcelSheetConverter withFieldConfiguration(Consumer<FieldConfiguration> consumer);

    <A extends Annotation> ExcelSheetConverter withSheetAnnotation(Class<A> clazz, BiConsumer<A, SheetConfiguration> consumer);

    <A extends Annotation> ExcelSheetConverter withFieldAnnotation(Class<A> clazz, BiConsumer<A, FieldConfiguration> consumer);

    ExcelSheetConverter withMapping(String fieldName);

    ExcelSheetConverter withMapping(String fieldName, String headerName);

    ExcelSheetConverter withMapping(String fieldName, String headerName, String styleAlias);

    ExcelSheetConverter withMapping(String fieldName, String headerName, StyleConfiguration styleConfiguration);

    ExcelSheetConverter withMapping(FieldConfiguration fieldConfiguration);
}
