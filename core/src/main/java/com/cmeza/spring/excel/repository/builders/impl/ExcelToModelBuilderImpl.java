package com.cmeza.spring.excel.repository.builders.impl;

import com.cmeza.spring.excel.repository.builders.ExtraBuilder;
import com.cmeza.spring.excel.repository.builders.ExcelToModelBuilder;
import com.cmeza.spring.excel.repository.builders.abstracts.AbstractBuilderExcel;
import com.cmeza.spring.excel.repository.converters.Converter;
import com.cmeza.spring.excel.repository.builders.ExcelToModelMapBuilder;
import com.cmeza.spring.excel.repository.support.configurations.model.AttributeConfiguration;
import com.cmeza.spring.excel.repository.support.converters.model.ToModelConverter;
import com.cmeza.spring.excel.repository.support.configurations.model.ModelConfiguration;
import com.cmeza.spring.excel.repository.support.converters.model.ToModelMapConverter;
import com.cmeza.spring.excel.repository.support.extensions.ValidatedExtension;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import com.cmeza.spring.excel.repository.support.results.*;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class ExcelToModelBuilderImpl<T, M> extends AbstractBuilderExcel<ExcelToModelBuilder<T>> implements ExcelToModelMapBuilder<T, M>, ExtraBuilder {

    private final ToModelConverter<T> converter;
    private final ValidatedExtension<T> validatedExtension;
    private final ApplicationContext applicationContext;

    public ExcelToModelBuilderImpl(Impl impl, Class<T> clazz) {
        this(impl, clazz, null);
    }

    public ExcelToModelBuilderImpl(Impl impl, Class<T> clazz, Class<M> mapClass) {
        super(impl);
        this.applicationContext = impl.getApplicationContext();
        this.converter = Converter.toModel(clazz, mapClass);
        this.validatedExtension = converter.toValidated();
    }

    @Override
    public ExcelToModelBuilder<T> withModelConfiguration(ModelConfiguration<T> modelConfiguration) {
        this.converter.withModelConfiguration(modelConfiguration);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withModelConfiguration(Consumer<ModelConfiguration<T>> consumer) {
        this.converter.withModelConfiguration(consumer);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withAttributeConfiguration(Consumer<AttributeConfiguration> consumer) {
        this.converter.withAttributeConfiguration(consumer);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withModelMapper(ToModelMapper<T> mapper) {
        this.converter.withModelMapper(mapper);
        return this;
    }

    @Override
    public ExcelToModelMapBuilder<T, M> withMapModel(MapModel<T, M> mapModel) {
        ((ToModelMapConverter<T, M>)this.converter).withMapModel(mapModel);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withSheetIndex(int sheetIndex) {
        this.converter.withSheetIndex(sheetIndex);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withSheetName(String sheetName) {
        this.converter.withSheetName(sheetName);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withRowCacheSize(int keepRowMemory) {
        this.converter.withRowCacheSize(keepRowMemory);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withBufferSize(int bufferSize) {
        this.converter.withBufferSize(bufferSize);
        return this;
    }

    @Override
    public <A extends Annotation> ExcelToModelBuilder<T> withModelAnnotation(Class<A> clazz, BiConsumer<A, ModelConfiguration<T>> consumer) {
        this.converter.withModelAnnotation(clazz, consumer);
        return this;
    }

    @Override
    public <A extends Annotation> ExcelToModelBuilder<T> withAttributeAnnotation(Class<A> clazz, BiConsumer<A, AttributeConfiguration> consumer) {
        this.converter.withAttributeAnnotation(clazz, consumer);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withMapping(String fieldName) {
        this.converter.withMapping(fieldName);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withMapping(String fieldName, String headerName) {
        this.converter.withMapping(fieldName, headerName);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withMapping(String fieldName, String headerName, Class<?> fieldType) {
        this.converter.withMapping(fieldName, headerName, fieldType);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withMapping(AttributeConfiguration attributeConfiguration) {
        this.converter.withMapping(attributeConfiguration);
        return this;
    }

    @Override
    public List<T> build(File excelFile) {
        return execute(() -> this.converter.build(excelFile), excelFile);
    }

    @Override
    public Validated<T> buildValidated(File excelFile) {
        return execute(() -> this.validatedExtension.buildValidated(excelFile), excelFile);
    }

    @Override
    public ExcelValidated<T> buildExcelValidated(File excelFile) {
        return execute(() -> this.validatedExtension.withErrorFile(true).buildExcelValidated(excelFile), excelFile);
    }

    @Override
    public ValidatedError<T> buildValidatedError(File excelFile) {
        return execute(() -> this.validatedExtension.buildValidatedError(excelFile), excelFile);
    }

    @Override
    public ExcelValidatedError<T> buildExcelValidatedError(File excelFile) {
        return execute(() -> this.validatedExtension.withErrorFile(true).buildExcelValidatedError(excelFile), excelFile);
    }

    @Override
    public ViewValidated<T> buildViewValidated(File excelFile) {
        return execute(() -> this.validatedExtension.withErrorFile(true).buildViewValidated(excelFile), excelFile);
    }

    @Override
    public ViewValidatedError<T> buildViewValidatedError(File excelFile) {
        return execute(() -> this.validatedExtension.withErrorFile(true).buildViewValidatedError(excelFile), excelFile);
    }

    @Override
    public List<M> buildMap(File excelFile) {
        return execute(() -> ((ToModelMapConverter<T, M>)this.converter).buildMap(excelFile), excelFile);
    }

    @Override
    public MapValidated<T, M> buildMapValidated(File excelFile) {
        return execute(() -> ((ToModelMapConverter<T, M>)this.converter).buildMapValidated(excelFile), excelFile);
    }

    @Override
    public MapExcelValidated<T, M> buildMapExcelValidated(File excelFile) {
        return execute(() -> {
            this.validatedExtension.withErrorFile(true);
            return ((ToModelMapConverter<T, M>)this.converter).buildMapExcelValidated(excelFile);
        }, excelFile);
    }

    @Override
    public MapValidatedError<T, M> buildMapValidatedError(File excelFile) {
        return execute(() -> ((ToModelMapConverter<T, M>)this.converter).buildMapValidatedError(excelFile), excelFile);
    }

    @Override
    public MapExcelValidatedError<T, M> buildMapExcelValidatedError(File excelFile) {
        return execute(() -> {
            this.validatedExtension.withErrorFile(true);
            return ((ToModelMapConverter<T, M>)this.converter).buildMapExcelValidatedError(excelFile);
        }, excelFile);
    }

    @Override
    public MapViewValidated<T, M> buildMapViewValidated(File excelFile) {
        return execute(() -> {
            this.validatedExtension.withErrorFile(true);
            return ((ToModelMapConverter<T, M>)this.converter).buildMapViewValidated(excelFile);
        }, excelFile);
    }

    @Override
    public MapViewValidatedError<T, M> buildMapViewValidatedError(File excelFile) {
        return execute(() -> {
            this.validatedExtension.withErrorFile(true);
            return ((ToModelMapConverter<T, M>)this.converter).buildMapViewValidatedError(excelFile);
        }, excelFile);
    }

    @Override
    public ExcelToModelBuilder<T> withErrorFolder(Path folder) {
        this.validatedExtension.withErrorFolder(folder);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withErrorFileName(String fileName) {
        this.validatedExtension.withErrorFileName(fileName);
        return this;
    }

    @Override
    public ExcelToModelBuilder<T> withErrorVersioned(boolean versioned) {
        this.validatedExtension.withErrorVersioned(versioned);
        return this;
    }

    @Override
    public void applyAfterMethodProcessor(ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        //ignored
    }

    @Override
    public void applyOnEndMethod() {
        ModelConfiguration<T> modelConfiguration = this.converter.getConfiguration();
        if (Objects.nonNull(modelConfiguration.getMapper())) {
            withModelMapper(BeanUtils.findBean(applicationContext, modelConfiguration.getMapper()));
        }
        if (Objects.nonNull(modelConfiguration.getMap())) {
            withMapModel((MapModel<T, M>)BeanUtils.findBean(applicationContext, modelConfiguration.getMap()));
        }

        try (LocalValidatorFactoryBean localValidatorFactoryBean = BeanUtils.findBean(applicationContext, LocalValidatorFactoryBean.class)) {
            this.converter.toValidated().withValidator(localValidatorFactoryBean.getValidator());
        }
    }

    @Override
    public void printExtras(Logger logger) {
        ModelConfiguration<T> modelConfiguration = this.converter.getConfiguration();

        if (Objects.nonNull(modelConfiguration.getSheetIndex())) {
            logger.info("| Sheet Index: {}", modelConfiguration.getSheetIndex());
        }

        if (Objects.nonNull(modelConfiguration.getSheetName())) {
            logger.info("| Sheet Name: {}", modelConfiguration.getSheetName());
        }

        logger.info("| Row cache size: {}", modelConfiguration.getRowCacheSize());
        logger.info("| Buffer size: {}", modelConfiguration.getBufferSize());
        logger.info("| Is hierarchical: {}", modelConfiguration.isHierarchical());
    }

    @Override
    protected boolean printResultValue() {
        return false;
    }

    @Override
    protected void printParameters(Logger log, boolean loggable, Map<String, Class<?>> parameters) {
        ModelConfiguration<T> modelConfiguration = this.converter.getConfiguration();
        modelConfiguration.getMappings().values().stream().filter(m -> !m.isIgnored()).forEach(m -> withParameter(m.getFieldName(), m.getFieldType()));
        super.printParameters(log, loggable, parameters);
    }

    @Override
    protected void printAdditionalParams(Logger log, boolean loggable, Object... params) {
        if (loggable) {
            log.info("| File: {}", params[0]);
        }
    }
}
