package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.dsl.models.ToExcelDsl;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToExcel;
import com.cmeza.spring.excel.repository.support.configurations.excel.ExcelConfiguration;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.support.parsers.excel.IExcelParser;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ExcelParser implements IExcelParser<ExcelConfiguration, ToExcel, ToExcelDsl> {

    @Override
    public void parse(ToExcel annotation, ExcelConfiguration excelConfiguration) {
        if (StringUtils.isNotEmpty(annotation.path())) {
            excelConfiguration.setPath(Path.of(annotation.path()));
        }

        if (StringUtils.isNotEmpty(annotation.fileName())) {
            excelConfiguration.setFileName(annotation.fileName());
        }

        if (StringUtils.isNotEmpty(annotation.prefix())) {
            excelConfiguration.setPrefix(annotation.prefix());
        }

        if (annotation.interceptor().length > 0) {
            excelConfiguration.setInterceptor(annotation.interceptor());
        }

        if (annotation.mapper().length > 0) {
            excelConfiguration.setMapper(annotation.mapper());
        }

        excelConfiguration.setVersioned(annotation.versioned());
        excelConfiguration.validate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void merge(ExcelConfiguration origin, ExcelConfiguration target) {
        if (Objects.nonNull(origin.getPath())) {
            target.setPath(origin.getPath());
        }
        if (StringUtils.isNotEmpty(origin.getFileName())) {
            target.setFileName(origin.getFileName());
        }
        if (StringUtils.isNotEmpty(origin.getPrefix())) {
            target.setPrefix(origin.getPrefix());
        }
        if (origin.isVersioned()) {
            target.setVersioned(true);
        }
        if (!origin.getStyles().isEmpty()) {
            target.getStyles().addAll(origin.getStyles());
        }
        if (Objects.nonNull(origin.getInterceptor()) && origin.getInterceptor().length > 0) {
            if (Objects.nonNull(target.getInterceptor())) {
                target.setInterceptor((Class<? extends ToExcelInterceptor>[]) Stream.concat(Arrays.stream(target.getInterceptor()), Arrays.stream(origin.getInterceptor()))
                        .toArray(Class<?>[]::new));
            } else {
                target.setInterceptor(origin.getInterceptor());
            }
        }
        if (Objects.nonNull(origin.getMapper()) && origin.getMapper().length > 0) {

            if (Objects.nonNull(target.getInterceptor())) {
                target.setMapper((Class<? extends ToExcelMapper>[]) Stream.concat(Arrays.stream(target.getMapper()), Arrays.stream(origin.getMapper()))
                        .toArray(Class<?>[]::new));
            } else {
                target.setMapper(origin.getMapper());
            }
        }
    }

    @Override
    public void parseDsl(ToExcel annotation, ToExcelDsl dsl) {
        if (annotation.loggable()) {
            dsl.setLoggable(true);
        }
        if (StringUtils.isNotEmpty(annotation.path())) {
            dsl.setPath(annotation.path());
        }

        if (StringUtils.isNotEmpty(annotation.fileName())) {
            dsl.setFileName(annotation.fileName());
        }

        if (StringUtils.isNotEmpty(annotation.prefix())) {
            dsl.setPrefix(annotation.prefix());
        }

        if (annotation.interceptor().length > 0) {
            dsl.setInterceptor(annotation.interceptor()[0]);
        }

        if (annotation.mapper().length > 0) {
            dsl.setMapper(annotation.mapper()[0]);
        }

        if (annotation.versioned()) {
            dsl.setVersioned(true);
        }
    }
}
