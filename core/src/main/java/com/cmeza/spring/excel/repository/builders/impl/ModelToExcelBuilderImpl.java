package com.cmeza.spring.excel.repository.builders.impl;

import com.cmeza.spring.excel.repository.builders.ExtraBuilder;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;
import com.cmeza.spring.excel.repository.builders.abstracts.AbstractBuilderExcel;
import com.cmeza.spring.excel.repository.dsl.properties.ExcelRepositoryProperties;
import com.cmeza.spring.excel.repository.contracts.ExcelContract;
import com.cmeza.spring.excel.repository.converters.Converter;
import com.cmeza.spring.excel.repository.support.converters.excel.ExcelSheetConverter;
import com.cmeza.spring.excel.repository.support.converters.excel.ToExcelConverter;
import com.cmeza.spring.excel.repository.support.configurations.excel.ExcelConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.View;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModelToExcelBuilderImpl extends AbstractBuilderExcel<ModelToExcelBuilder> implements ModelToExcelBuilder, ExtraBuilder {

    private final ToExcelConverter toExcelConverter;
    private final ExcelRepositoryProperties excelRepositoryProperties;
    private final ApplicationContext applicationContext;

    public ModelToExcelBuilderImpl(Impl impl) {
        super(impl);
        this.toExcelConverter = Converter.toExcel();
        this.excelRepositoryProperties = impl.getExcelRepositoryProperties();
        this.applicationContext = impl.getApplicationContext();
    }

    @Override
    public ModelToExcelBuilder withConfiguration(ExcelConfiguration excelConfiguration) {
        this.toExcelConverter.withConfiguration(excelConfiguration);
        return this;
    }

    @Override
    public ModelToExcelBuilder withConfiguration(Consumer<ExcelConfiguration> consumer) {
        this.toExcelConverter.withConfiguration(consumer);
        return this;
    }

    @Override
    public ExcelSheetConverter sheet() {
        return this.toExcelConverter.sheet();
    }

    @Override
    public ExcelSheetConverter sheet(int index) {
        return this.toExcelConverter.sheet(index);
    }

    @Override
    public ModelToExcelBuilder withSheet(Consumer<ExcelSheetConverter> sheetConsumer) {
        this.toExcelConverter.withSheet(sheetConsumer);
        return this;
    }

    @Override
    public ModelToExcelBuilder withInterceptor(ToExcelInterceptor interceptor) {
        this.toExcelConverter.withIntercertor(interceptor);
        return this;
    }

    @Override
    public ModelToExcelBuilder withMapper(ToExcelMapper mapper) {
        this.toExcelConverter.withMapper(mapper);
        return this;
    }

    @Override
    public ModelToExcelBuilder withPath(Path path) {
        this.toExcelConverter.withPath(path);
        return this;
    }

    @Override
    public ModelToExcelBuilder withFileName(String fileName) {
        this.toExcelConverter.withFileName(fileName);
        return this;
    }

    @Override
    public Path buildFile() {
        return execute(this.toExcelConverter::buildFile);
    }

    @Override
    public View buildView() {
        return execute(this.toExcelConverter::buildView);
    }

    @Override
    public ExcelConfiguration getExcelConfiguration() {
        return this.toExcelConverter.getExcelConfiguration();
    }

    @Override
    public void printExtras(Logger logger) {
        this.toExcelConverter.preBuilt();
        ExcelConfiguration excelConfiguration = this.toExcelConverter.getExcelConfiguration();
        logger.info("| Path: {}", excelConfiguration.getPath());
        if (StringUtils.isNotEmpty(excelConfiguration.getPrefix())) {
            logger.info("| Filename: {}{}", excelConfiguration.getPrefix(), excelConfiguration.getFileName());
            logger.info("| Prefix: {}", excelConfiguration.getPrefix());
        } else {
            logger.info("| Filename: {}", excelConfiguration.getFileName());
        }
        if (Objects.nonNull(excelConfiguration.getInterceptor())) {
            Arrays.stream(excelConfiguration.getInterceptor()).forEach(interceptor ->
                    logger.info("| Interceptor: {}", interceptor));
        }
        if (Objects.nonNull(excelConfiguration.getMapper())) {
            Arrays.stream(excelConfiguration.getMapper()).forEach(mapper ->
                    logger.info("| Mapper: {}", mapper));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void applyAfterMethodProcessor(ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        List<StyleConfiguration> classStyleConfigurations = classMetadata.getAttribute(ExcelContract.CLASS_STYLE_CONFIGURATIONS, List.class, new ArrayList<>());
        if (Objects.nonNull(excelRepositoryProperties.getGlobalStyleConfigurationBean())) {
            classStyleConfigurations.addAll(Arrays.stream(excelRepositoryProperties.getGlobalStyleConfigurationBean())
                    .map(style -> BeanUtils.findBean(applicationContext, StyleConfiguration.class, style))
                    .filter(style -> classStyleConfigurations.stream().noneMatch(s -> s.getAlias().equals(style.getAlias())))
                    .collect(Collectors.toList()));
        }

        if (!classStyleConfigurations.isEmpty()) {
            withConfiguration(excelConfiguration ->
                    classStyleConfigurations.forEach(excelConfiguration::addStyle));
        }
    }

    @Override
    public void applyOnEndMethod() {
        ExcelConfiguration excelConfiguration = this.toExcelConverter.getExcelConfiguration();
        if (Objects.nonNull(excelConfiguration.getInterceptor())) {
            Arrays.stream(excelConfiguration.getInterceptor()).forEach(interceptor ->
                    withInterceptor(BeanUtils.findBean(applicationContext, interceptor)));
        }
        if (Objects.nonNull(excelConfiguration.getMapper())) {
            Arrays.stream(excelConfiguration.getMapper()).forEach(mapper ->
                    withMapper(BeanUtils.findBean(applicationContext, mapper)));
        }
    }
}
