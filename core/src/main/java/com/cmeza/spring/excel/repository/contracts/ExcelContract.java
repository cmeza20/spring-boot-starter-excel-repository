package com.cmeza.spring.excel.repository.contracts;

import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.builders.ExtraBuilder;
import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.configurations.ExcelRepositoryProperties;
import com.cmeza.spring.excel.repository.support.exceptions.ContractException;
import com.cmeza.spring.excel.repository.repositories.definitions.ParameterDefinition;
import com.cmeza.spring.excel.repository.repositories.configurations.SimpleExcelConfigurationImpl;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelExecutor;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.ioc.handler.contracts.IocContract;
import com.cmeza.spring.ioc.handler.contracts.consumers.ClassConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.MethodConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.ParameterConsumer;
import com.cmeza.spring.ioc.handler.contracts.consumers.enums.ConsumerLocation;
import com.cmeza.spring.ioc.handler.contracts.consumers.manager.ConsumerManager;
import com.cmeza.spring.ioc.handler.handlers.IocTarget;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import com.cmeza.spring.ioc.handler.metadata.impl.SimpleTypeMetadata;
import com.cmeza.spring.ioc.handler.processors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@RequiredArgsConstructor
public class ExcelContract implements IocContract<ExcelRepository>, ApplicationContextAware {
    private final List<AnnotatedClassProcessor<?>> classProcessors;
    private final List<AnnotatedMethodProcessor<?>> methodProcessors;
    private final List<AnnotatedParameterProcessor<?>> parameterProcessors;
    private final List<SimpleParameterProcessor> simpleParameterProcessors;
    private ExcelRepositoryProperties excelRepositoryProperties;
    private ApplicationContext applicationContext;
    private ExcelRepositoryTemplate excelRepositoryTemplate;

    public static final String METHOD_BUILDER = "METHOD_BUILDER";
    public static final String METHOD_CONFIGURATION = "METHOD_CONFIGURATION";
    public static final String METHOD_EXECUTOR = "METHOD_EXECUTOR";
    public static final String METHOD_PARAMETERS = "METHOD_PARAMETERS";
    public static final String METHOD_LOGGABLE = "METHOD_LOGGABLE";
    public static final String METHOD_SHEET_CONFIGURATIONS = "METHOD_SHEET_CONFIGURATIONS";
    public static final String METHOD_STYLE_CONFIGURATIONS = "METHOD_STYLE_CONFIGURATIONS";
    public static final String CLASS_STYLE_CONFIGURATIONS = "CLASS_STYLE_CONFIGURATIONS";
    public static final String PARAMETER_NAME = "PARAMETER_NAME";

    private final ClassConsumer afterAnnotationClassProcessor = (classMetadata ->
            excelRepositoryTemplate = applicationContext.getBean(ExcelRepositoryTemplate.class));

    private final MethodConsumer onStartMethodConsumer = (classMetadata, methodMetadata) -> {
        if (!methodMetadata.isIntercepted()) {
            SimpleExcelConfigurationImpl.Builder configuration = SimpleExcelConfigurationImpl.builder()
                    .excelTemplate(excelRepositoryTemplate)
                    .typeMetadata(methodMetadata.getTypeMetadata())
                    .configKey(methodMetadata.getConfigKey());

            methodMetadata.addAttribute(METHOD_CONFIGURATION, configuration);
            methodMetadata.addAttribute(METHOD_PARAMETERS, new ParameterDefinition[methodMetadata.getMethod().getParameterCount()]);
            methodMetadata.addAttribute(METHOD_SHEET_CONFIGURATIONS, new LinkedHashMap<>());
            methodMetadata.addAttribute(METHOD_STYLE_CONFIGURATIONS, new LinkedList<>());
        }
    };

    private final MethodConsumer afterAnnotationMethodProcessor = (classMetadata, methodMetadata) -> {
        if (!methodMetadata.isIntercepted()) {
            Assert.isTrue(methodMetadata.hasAttribute(METHOD_EXECUTOR), methodMetadata.getConfigKey() + " - needs an annotation: @ToExcel or @ToModel");

            ExcelGenericBuilder<?> builder = methodMetadata.getAttribute(ExcelContract.METHOD_BUILDER, ExcelGenericBuilder.class);
            if (builder instanceof ExtraBuilder) {
                ((ExtraBuilder)builder).applyAfterMethodProcessor(classMetadata, methodMetadata);
            }
        }
    };

    private final ParameterConsumer onEndParameterConsumer = (classMetadata, methodMetadata, parameterMetadata, index) -> {
        if (!methodMetadata.isIntercepted()) {
            ParameterDefinition[] parameters = methodMetadata.getAttribute(METHOD_PARAMETERS, ParameterDefinition[].class);

            String name = parameterMetadata.getAttribute(PARAMETER_NAME, String.class);
            TypeMetadata typeMetadata = parameterMetadata.getTypeMetadata();
            boolean isRawTypeCustomArgument = false;
            if (typeMetadata.isArray() && Objects.nonNull(typeMetadata.getArgumentClass())) {
                SimpleTypeMetadata simpleTypeMetadata = new SimpleTypeMetadata(typeMetadata.getArgumentClass());
                isRawTypeCustomArgument = simpleTypeMetadata.isCustomArgumentObject();
            }

            ParameterDefinition parameterDefinition = new ParameterDefinition();
            parameterDefinition.setParameterName(Objects.nonNull(name) ? name : parameterMetadata.getParameter().getName());
            parameterDefinition.setPosition(index);
            parameterDefinition.setTypeMetadata(typeMetadata);
            parameterDefinition.setArray(typeMetadata.isArray());
            parameterDefinition.setCollection(typeMetadata.isList() || typeMetadata.isSet() || typeMetadata.isStream() || typeMetadata.isArray());
            parameterDefinition.setBean(typeMetadata.isCustomArgumentObject() && !typeMetadata.isList() && !typeMetadata.isMap() && !typeMetadata.isSet() && !typeMetadata.isArray() && !typeMetadata.isStream());
            parameterDefinition.setBatch(isRawTypeCustomArgument || (typeMetadata.isCustomArgumentObject() && (typeMetadata.isList() || typeMetadata.isMap() || typeMetadata.isSet() || typeMetadata.isStream())));
            parameterDefinition.setPath(typeMetadata.getRawClass().isAssignableFrom(Path.class));
            parameterDefinition.setFile(typeMetadata.getRawClass().isAssignableFrom(File.class));
            parameters[index] = parameterDefinition;

            methodMetadata.addAttribute(METHOD_PARAMETERS, parameters);
        }
    };

    protected final MethodConsumer onEndMethodConsumer = (classMetadata, methodMetadata) -> {
        if (!methodMetadata.isIntercepted()) {
            ExcelGenericBuilder<?> builder = methodMetadata.getAttribute(METHOD_BUILDER, ExcelGenericBuilder.class);
            if (Objects.isNull(builder)) {
                throw new ContractException("Method " + methodMetadata.getName() + " has no builder");
            }

            if (builder instanceof ExtraBuilder) {
                ((ExtraBuilder)builder).applyOnEndMethod();
            }

            ExcelExecutor executor = methodMetadata.getAttribute(METHOD_EXECUTOR, ExcelExecutor.class);
            if (Objects.isNull(executor)) {
                throw new ContractException("Method " + methodMetadata.getName() + " has no executor");
            }

            SimpleExcelConfigurationImpl.Builder configuration = methodMetadata.getAttribute(METHOD_CONFIGURATION, SimpleExcelConfigurationImpl.Builder.class);
            configuration.loggable(excelRepositoryProperties.isLoggable() || methodMetadata.getAttribute(METHOD_LOGGABLE, Boolean.class, false));
            configuration.targetClass(classMetadata.getTargetClass());

            //Builder
            configuration.builder(builder);

            //Parameters
            configuration.parameters(methodMetadata.getAttribute(METHOD_PARAMETERS, ParameterDefinition[].class, new ParameterDefinition[0]));

            executor.attachConfiguration(configuration.build());
            executor.print();
            methodMetadata.addAttribute(METHOD_EXECUTOR, executor);
        }
    };

    @Override
    public void configure(ConsumerManager consumerManager) {
        consumerManager
                .addClassConsumer(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR, afterAnnotationClassProcessor)
                .addMethodConsumer(ConsumerLocation.ON_START, onStartMethodConsumer)
                .addMethodConsumer(ConsumerLocation.AFTER_ANNOTATION_PROCESSOR, afterAnnotationMethodProcessor)
                .addParameterConsumer(ConsumerLocation.ON_END, onEndParameterConsumer)
                .addMethodConsumer(ConsumerLocation.ON_END, onEndMethodConsumer);
    }

    @Override
    public void processors(IocProcessors processors) {
        processors.clearAnnotatedClassProcessors();
        processors.clearAnnotatedMethodProcessors();
        processors.clearAnnotatedParameterProcessors();
        processors.clearSimpleParameterProcessors();
        processors.setAnnotatedClassProcessors(classProcessors);
        processors.setAnnotatedMethodProcessors(methodProcessors);
        processors.setAnnotatedParameterProcessors(parameterProcessors);
        processors.setSimpleParameterProcessors(simpleParameterProcessors);
    }

    @Override
    public Object execute(ClassMetadata classMetadata, MethodMetadata methodMetadata, Object[] arguments, IocTarget<?> target) {
        return methodMetadata.getAttribute(METHOD_EXECUTOR, ExcelExecutor.class).execute(methodMetadata, arguments);
    }

    @Override
    public boolean onlyDeclaredMethods() {
        return excelRepositoryProperties.isOnlyDeclaredMethods();
    }

    @Override
    public boolean onlyMethodDeclaredAnnotations() {
        return excelRepositoryProperties.isOnlyMethodDeclaredAnnotations();
    }

    @Override
    public boolean onlyParameterDeclaredAnnotations() {
        return excelRepositoryProperties.isOnlyParameterDeclaredAnnotations();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.excelRepositoryProperties = applicationContext.getBean(ExcelRepositoryProperties.class);
    }
}
