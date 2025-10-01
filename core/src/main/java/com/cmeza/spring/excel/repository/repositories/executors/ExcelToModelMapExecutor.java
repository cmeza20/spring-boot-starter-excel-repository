package com.cmeza.spring.excel.repository.repositories.executors;

import com.cmeza.spring.excel.repository.repositories.configurations.SimpleExcelConfiguration;
import com.cmeza.spring.excel.repository.repositories.definitions.ParameterDefinition;
import com.cmeza.spring.excel.repository.repositories.executors.abstracts.AbstractExcelExecutor;
import com.cmeza.spring.excel.repository.repositories.executors.types.ExecutorType;
import com.cmeza.spring.excel.repository.repositories.executors.types.ReturnType;
import com.cmeza.spring.excel.repository.repositories.executors.types.impl.ExcelToModelMapReturnType;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToModel;
import com.cmeza.spring.excel.repository.builders.ExcelToModelMapBuilder;
import com.cmeza.spring.excel.repository.support.exceptions.ExecuteUnsupportedException;
import com.cmeza.spring.excel.repository.support.results.*;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ExcelToModelMapExecutor extends AbstractExcelExecutor<ExcelToModelMapBuilder<Object, Object>> {

    private final ToModel toModel;

    public ExcelToModelMapExecutor(ToModel toModel) {
        this.toModel = toModel;
    }

    @Override
    protected Object execute(ReturnType returnType, ExcelToModelMapBuilder<Object, Object> excelBuilder, TypeMetadata typeMetadata, SimpleExcelConfiguration configuration, boolean isBatch, Object[] arguments) {
        ExcelToModelMapReturnType excelToModelMapReturnType = ExcelToModelMapReturnType.from(returnType);
        return excelToModelMapReturnType.execute(excelBuilder, arguments);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ExcelToModelMapBuilder<Object, Object> bindBuilder(ExcelRepositoryTemplate excelRepositoryTemplate, SimpleExcelConfiguration configuration) {
        return (ExcelToModelMapBuilder<Object, Object>) configuration.getBuilder()
                .withKey(configuration.getConfigKey())
                .loggable(toModel.loggable());
    }

    @Override
    protected ReturnType bindReturnType(TypeMetadata typeMetadata) {
        return ReturnType.forToModel(typeMetadata);
    }

    @Override
    public ExecutorType getExecuteType() {
        return ExecutorType.EXCEL_TO_MODEL;
    }

    @Override
    public void validateConfiguration(SimpleExcelConfiguration excelConfiguration) {
        List<Class<?>> availables = List.of(
                List.class,
                Set.class,
                Stream.class,
                ExcelValidated.class,
                ExcelValidatedError.class,
                MapExcelValidated.class,
                MapExcelValidatedError.class,
                MapValidated.class,
                MapValidatedError.class,
                MapViewValidated.class,
                MapViewValidatedError.class,
                Validated.class,
                ValidatedError.class,
                ViewValidated.class,
                ViewValidatedError.class
        );

        requiredReturnTypes(excelConfiguration, availables, (configKey, types) -> {
            throw new IllegalArgumentException(configKey + " - Only " + types + " is supported");
        });
    }

    @Override
    protected void eachParameter(ExcelToModelMapBuilder<Object, Object> builder, ParameterDefinition parameterDefinition, Object obj, SimpleExcelConfiguration excelConfiguration, int index) {
        if (!parameterDefinition.isPath() && !parameterDefinition.isFile()) {
            throw new ExecuteUnsupportedException(String.format("%s - The Parameter %s has an incorrect type, only File or Path is available", excelConfiguration.getConfigKey(), parameterDefinition.getParameterName()));
        }
    }

    @Override
    protected void bindParameters(ExcelToModelMapBuilder<Object, Object> builder, List<Parameter> parameters) {
        //ignore
    }
}
