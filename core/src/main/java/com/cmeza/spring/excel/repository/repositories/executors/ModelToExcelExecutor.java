package com.cmeza.spring.excel.repository.repositories.executors;

import com.cmeza.spring.excel.repository.support.annotations.methods.ToExcel;
import com.cmeza.spring.excel.repository.builders.ModelToExcelBuilder;
import com.cmeza.spring.excel.repository.support.exceptions.ExecuteUnsupportedException;
import com.cmeza.spring.excel.repository.repositories.configurations.SimpleExcelConfiguration;
import com.cmeza.spring.excel.repository.repositories.definitions.ParameterDefinition;
import com.cmeza.spring.excel.repository.repositories.executors.abstracts.AbstractExcelExecutor;
import com.cmeza.spring.excel.repository.repositories.executors.types.ExecutorType;
import com.cmeza.spring.excel.repository.repositories.executors.types.ReturnType;
import com.cmeza.spring.excel.repository.repositories.executors.types.impl.ModelToExcelReturnType;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import org.springframework.web.servlet.View;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class ModelToExcelExecutor extends AbstractExcelExecutor<ModelToExcelBuilder> {

    private final ToExcel toExcel;

    public ModelToExcelExecutor(ToExcel toExcel) {
        this.toExcel = toExcel;
    }

    @Override
    protected Object execute(ReturnType returnType, ModelToExcelBuilder builder, TypeMetadata typeMetadata, SimpleExcelConfiguration configuration, boolean isBatch, Object[] arguments) {
        ModelToExcelReturnType modelToExcelReturnType = ModelToExcelReturnType.from(returnType);
        return modelToExcelReturnType.execute(builder, arguments);
    }

    @Override
    protected ModelToExcelBuilder bindBuilder(ExcelRepositoryTemplate excelRepositoryTemplate, SimpleExcelConfiguration configuration) {
        return (ModelToExcelBuilder) configuration.getBuilder()
                .withKey(configuration.getConfigKey())
                .loggable(toExcel.loggable());
    }

    @Override
    protected ReturnType bindReturnType(TypeMetadata typeMetadata) {
       return ReturnType.forToExcel(typeMetadata);
    }

    @Override
    public ExecutorType getExecuteType() {
        return ExecutorType.MODEL_TO_EXCEL;
    }

    @Override
    public void validateConfiguration(SimpleExcelConfiguration excelConfiguration) {
        List<Class<?>> availables = List.of(
                View.class,
                Path.class,
                File.class
        );
        requiredReturnTypes(excelConfiguration, availables, (configKey, types) -> {
            throw new IllegalArgumentException(configKey + " - Only " + types + " is supported");
        });
    }

    @Override
    @SuppressWarnings("all")
    protected void eachParameter(ModelToExcelBuilder builder, ParameterDefinition parameterDefinition, Object obj, SimpleExcelConfiguration excelConfiguration, int index) {
        if (!parameterDefinition.isCollection()) {
            throw new ExecuteUnsupportedException(String.format("%s - The Parameter %s has an incorrect type, only Collections is available", excelConfiguration.getConfigKey(), parameterDefinition.getParameterName()));
        }

        builder.sheet(index).withData((Collection)obj);
    }
}
