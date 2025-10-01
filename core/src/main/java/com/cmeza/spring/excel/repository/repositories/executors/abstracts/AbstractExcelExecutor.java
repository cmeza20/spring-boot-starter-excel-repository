package com.cmeza.spring.excel.repository.repositories.executors.abstracts;

import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.repositories.configurations.SimpleExcelConfiguration;
import com.cmeza.spring.excel.repository.repositories.definitions.ParameterDefinition;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelExecutor;
import com.cmeza.spring.excel.repository.repositories.executors.types.ReturnType;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractExcelExecutor<T> implements ExcelExecutor {

    private SimpleExcelConfiguration configuration;

    protected abstract Object execute(ReturnType returnType, T excelBuilder, TypeMetadata typeMetadata, SimpleExcelConfiguration configuration, boolean isBatch, Object[] arguments);

    protected abstract T bindBuilder(ExcelRepositoryTemplate excelRepositoryTemplate, SimpleExcelConfiguration configuration);

    protected abstract ReturnType bindReturnType(TypeMetadata typeMetadata);

    @Override
    public Object execute(MethodMetadata methodMetadata, Object[] arguments) {
        TypeMetadata typeMetadata = configuration.getTypeMetadata();
        ExcelRepositoryTemplate excelTemplate = configuration.getExcelTemplate();

        T builder = bindBuilder(excelTemplate, configuration);

        List<Parameter> parameters = new ArrayList<>();

        int batchCount = this.processBuilder(builder, arguments, configuration.getParameters(), parameters, configuration);

        boolean isBatch = batchCount > 0;
        ReturnType returnType = this.bindReturnType(typeMetadata);

        this.bindParameters(builder, parameters);

        return this.execute(returnType, builder, typeMetadata, configuration, isBatch, arguments);
    }

    @Override
    public void attachConfiguration(SimpleExcelConfiguration excelConfiguration) {
        this.configuration = excelConfiguration;
        this.validateConfiguration();
        this.validateConfiguration(excelConfiguration);
    }

    @Override
    public void validateConfiguration(SimpleExcelConfiguration excelConfiguration) {
    }

    @Override
    public void print() {
        if (log.isDebugEnabled()) {
            log.debug("Registered ExcelRepository: [{}] {}", getExecuteType(), configuration.getConfigKey());
        }
    }

    private void validateConfiguration() {
        Assert.notNull(configuration, "ExcelConfiguration required");
    }

    private int processBuilder(T builder, Object[] arguments, ParameterDefinition[] parameterDefinitions, List<Parameter> parameters, SimpleExcelConfiguration excelConfiguration) {
        Assert.notNull(builder, "ExcelBuilder required");

        int index = 0;
        int batchCount = 0;
        for (Object obj : arguments) {
            ParameterDefinition definition = parameterDefinitions[index];
            batchCount += bindParamaters(builder, definition, obj, parameters, excelConfiguration, index);
            index++;
        }

        return batchCount;
    }

    @SuppressWarnings("unchecked")
    protected void bindParameters(T builder, List<Parameter> parameters) {
        parameters.forEach(param -> ((ExcelGenericBuilder<T>)builder).withParameter(param.getName(), param.getValue()));
    }

    protected void eachParameter(T builder, ParameterDefinition parameterDefinition, Object obj, SimpleExcelConfiguration excelConfiguration, int index) {
    }

    protected void requiredReturnTypes(SimpleExcelConfiguration excelConfiguration, List<Class<?>> supportTypes, BiConsumer<String, String> errorConsumer) {
        TypeMetadata typeMetadata = excelConfiguration.getTypeMetadata();
        boolean exists = supportTypes.stream().anyMatch(type -> typeMetadata.isAssignableFrom(typeMetadata.getRawClass(), type));
        if (!exists) {
            String types = supportTypes.stream().map(Class::getSimpleName).collect(Collectors.joining(", "));
            errorConsumer.accept(excelConfiguration.getConfigKey(), types);
        }
    }

    private int bindParamaters(T builder, ParameterDefinition definition, Object obj, List<Parameter> parameters, SimpleExcelConfiguration excelConfiguration, int index) {
        int batchCount = 0;
        if (Objects.nonNull(definition)) {
            if (Objects.isNull(obj)) {
                this.eachParameter(builder, definition, obj, excelConfiguration, index);
                parameters.add(new Parameter(definition, obj, false));
                return 0;
            }

            Object inParam = obj.getClass().isArray() ? Arrays.asList((Object[]) obj) : obj;
            this.eachParameter(builder, definition, inParam, excelConfiguration, index);

            if (definition.isBean() || definition.isBatch()) {
                parameters.add(new Parameter(definition, inParam, true));
                if (definition.isCollection()) {
                    batchCount++;
                }
            } else {
                parameters.add(new Parameter(definition, inParam, false));
            }

        }
        return batchCount;
    }

    @Getter
    public static class Parameter {
        private final String name;
        private final Object value;
        private final boolean isObject;

        public Parameter(String name) {
            this.name = name;
            this.isObject = false;
            this.value = null;
        }

        public Parameter(ParameterDefinition parameterDefinition, Object value, boolean isObject) {
            this.name = parameterDefinition.getParameterName();
            this.value = value;
            this.isObject = isObject;
        }

    }
}
