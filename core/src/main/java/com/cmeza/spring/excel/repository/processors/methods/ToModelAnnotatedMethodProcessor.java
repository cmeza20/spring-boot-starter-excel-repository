package com.cmeza.spring.excel.repository.processors.methods;

import com.cmeza.spring.excel.repository.dsl.properties.DslProperties;
import com.cmeza.spring.excel.repository.parsers.model.ModelParser;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelToModelMapExecutor;
import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToModel;
import com.cmeza.spring.excel.repository.support.annotations.model.Column;
import com.cmeza.spring.excel.repository.support.annotations.support.Error;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.builders.ExcelToModelBuilder;
import com.cmeza.spring.excel.repository.support.configurations.model.ErrorConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.model.ModelConfiguration;
import com.cmeza.spring.excel.repository.dsl.models.ErrorDsl;
import com.cmeza.spring.excel.repository.dsl.models.ToModelDsl;
import com.cmeza.spring.excel.repository.processors.abstracts.AbstractAnnotatedMethodProcessor;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelExecutor;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelToModelExecutor;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.excel.repository.utils.BeanUtils;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ToModelAnnotatedMethodProcessor<T, M> extends AbstractAnnotatedMethodProcessor<ToModel, ExcelGenericBuilder<?>, ToModelDsl> {

    private Class<T> modelClass;
    private Class<M> mapClass;

    public ToModelAnnotatedMethodProcessor(DslProperties dslProperties) {
        super(dslProperties);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure(ToModel annotation, ExcelRepository excelRepository, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        modelClass = (Class<T>) methodMetadata.getTypeMetadata().getArgumentClass();

        Class<?>[] argumentClass = methodMetadata.getTypeMetadata().getArgumentTypes();
        if (argumentClass.length > 1) {
            mapClass = (Class<M>) argumentClass[1];
        }
    }

    @Override
    protected ExcelExecutor executorProcess(ExcelRepository excelRepository, ToModel annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        if (Objects.nonNull(mapClass)) {
            return new ExcelToModelMapExecutor(annotation);
        }
        return new ExcelToModelExecutor(annotation);
    }

    @Override
    protected ExcelGenericBuilder<?> builder(ExcelRepository excelRepository, ToModel annotation, ExcelRepositoryTemplate excelRepositoryTemplate, MethodMetadata methodMetadata) {
        ModelConfiguration<T> modelConfiguration = this.generateConfiguration(annotation);
        ErrorConfiguration errorConfiguration = modelConfiguration.getError();

        ExcelToModelBuilder<T> excelToModelBuilder;
        if (Objects.isNull(mapClass)) {
            excelToModelBuilder = excelRepositoryTemplate.toModel(modelClass);
        } else {
            excelToModelBuilder = excelRepositoryTemplate.toModel(modelClass, mapClass);
        }

        excelToModelBuilder.withModelConfiguration(modelConfiguration)
                .withErrorFileName(errorConfiguration.getFileName())
                .withErrorFolder(errorConfiguration.getFolder())
                .withErrorVersioned(errorConfiguration.isVersioned());
        this.bindColumnAnnotation(excelToModelBuilder);
        return excelToModelBuilder;
    }

    @Override
    protected ToModelDsl dslLocator(ToModel annotation, DslProperties dslProperties, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        ToModelDsl toModelDsl = dslProperties.findToModelDsl(classMetadata.getTargetClass().getSimpleName(), methodMetadata.getMethod().getName());
        Parser.getInstance().getParser(ModelParser.class).parseDsl(annotation, toModelDsl);
        return toModelDsl;
    }

    @Override
    protected void resolvePlaceholders(ToModelDsl dslProperty) {
        ErrorDsl errorDsl = dslProperty.getError();
        if (StringUtils.isNotEmpty(errorDsl.getFolder())) {
            errorDsl.setFolder(propertiesResolver.resolveRequiredPlaceholders(errorDsl.getFolder()));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void updateValues(Map<String, Object> values, ToModelDsl dslProperty) {
        List<Map<String, Object>> mappingList = (List<Map<String, Object>>) values.get("mapping");

        Mapping[] mappings = Objects.nonNull(mappingList) ? new Mapping[mappingList.size()] : new Mapping[0];
        if (Objects.nonNull(mappingList)) {
            int pos = 0;
            for (Map<String, Object> map: mappingList) {
                mappings[pos] = ExcelUtils.createAnnotation(Mapping.class, map);
                pos++;
            }
        }

        Error error = ExcelUtils.createAnnotation(Error.class, (Map<String, Object>) values.get("error"));

        values.put("error", error);
        values.put("mapping", mappings);
    }

    private ModelConfiguration<T> applyModelConfiguration(ToModel annotation, ModelConfiguration<T> modelConfiguration) {
        PARSER.getParser(ModelParser.class).parse(annotation, modelConfiguration);
        return modelConfiguration;
    }

    @SuppressWarnings("unchecked")
    private ModelConfiguration<T> generateConfiguration(ToModel annotation) {
        String beanName = annotation.modelConfigurationBean();
        if (StringUtils.isEmpty(beanName)) {
            return applyModelConfiguration(annotation, new ModelConfiguration<>());
        }

        ModelConfiguration<T> modelConfigurationBean = BeanUtils.findBean(applicationContext, ModelConfiguration.class, beanName);
        return applyModelConfiguration(annotation, modelConfigurationBean.cloneInstance());
    }

    private void bindColumnAnnotation(ExcelToModelBuilder<T> excelToModelBuilder) {
        excelToModelBuilder.withAttributeAnnotation(Column.class, (column, attributeConfiguration) -> {
            if (StringUtils.isNotEmpty(column.header())) {
                attributeConfiguration.setHeaderName(column.header());
            }
            if (StringUtils.isNotEmpty(column.value())) {
                attributeConfiguration.setHeaderName(column.value());
            }
            if (StringUtils.isNotEmpty(column.mapping())) {
                attributeConfiguration.setFieldName(column.mapping());
            }
            if (column.ignored()) {
                attributeConfiguration.setIgnored(true);
            }
        });
    }

}
