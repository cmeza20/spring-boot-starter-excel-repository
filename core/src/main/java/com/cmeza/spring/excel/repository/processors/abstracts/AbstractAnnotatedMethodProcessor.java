package com.cmeza.spring.excel.repository.processors.abstracts;

import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.aware.ExcelRepositoryAware;
import com.cmeza.spring.excel.repository.support.builders.ExcelGenericBuilder;
import com.cmeza.spring.excel.repository.contracts.ExcelContract;
import com.cmeza.spring.excel.repository.support.transform.Transform;
import com.cmeza.spring.excel.repository.dsl.properties.DslProperties;
import com.cmeza.spring.excel.repository.repositories.executors.ExcelExecutor;
import com.cmeza.spring.excel.repository.resolvers.ExcelPropertyResolver;
import com.cmeza.spring.excel.repository.templates.ExcelRepositoryTemplate;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import com.cmeza.spring.ioc.handler.metadata.AnnotationMetadata;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.processors.AnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.utils.IocUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractAnnotatedMethodProcessor<A extends Annotation, B extends ExcelGenericBuilder<?>, D extends Transform<Map<String, Object>>> implements AnnotatedMethodProcessor<A>, ExcelRepositoryAware, ApplicationContextAware {
    protected static final String LOGGABLE = "loggable";
    protected static final Parser PARSER = Parser.getInstance();
    protected final DslProperties dslProperties;
    protected ExcelRepositoryTemplate excelRepositoryTemplate;
    protected ApplicationContext applicationContext;
    protected ExcelPropertyResolver propertiesResolver;

    protected AbstractAnnotatedMethodProcessor(DslProperties dslProperties) {
        this.dslProperties = dslProperties;
    }

    protected abstract void configure(A annotation, ExcelRepository excelRepository, ClassMetadata classMetadata, MethodMetadata methodMetadata);

    protected abstract ExcelExecutor executorProcess(ExcelRepository excelRepository, A annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata);

    protected abstract B builder(ExcelRepository excelRepository, A annotation, ExcelRepositoryTemplate excelRepositoryTemplate, MethodMetadata methodMetadata);

    protected abstract D dslLocator(A annotation, DslProperties dslProperties, ClassMetadata classMetadata, MethodMetadata methodMetadata, String dslName);

    protected abstract void resolvePlaceholders(D dslProperty, ClassMetadata classMetadata, MethodMetadata methodMetadata);

    protected abstract void updateValues(Map<String, Object> values, D dslProperty);

    @Override
    public A process(AnnotationMetadata<A> annotationMetadata, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        Assert.isTrue(!methodMetadata.hasAttribute(ExcelContract.METHOD_EXECUTOR), "Only one annotation per method is allowed.");
        A annotation = annotationMetadata.getAnnotation();
        ExcelRepository excelRepository = classMetadata.getProcessorResult(ExcelRepository.class);

        //Configure
        this.configure(annotation, excelRepository, classMetadata, methodMetadata);

        //DslName
        String dslClassName = classMetadata.getTargetClass().getSimpleName();
        if (StringUtils.isNotEmpty(excelRepository.dslName())) {
            dslClassName = excelRepository.dslName();
        }

        //Dsl
        D dslProperty = this.dslLocator(annotation, dslProperties, classMetadata, methodMetadata, dslClassName);

        //Resolve placeholders
        this.resolvePlaceholders(dslProperty, classMetadata, methodMetadata);

        //Map Values
        Map<String, Object> mapValues = this.bindMapValues(excelRepository, dslProperty, methodMetadata);

        //Update Values
        this.updateValues(mapValues, dslProperty);

        //update annotation
        annotation = ExcelUtils.updateAnnotation(annotation, mapValues);

        //Executor Process
        ExcelExecutor executor = this.executorProcess(excelRepository, annotation, classMetadata, methodMetadata);
        this.bindExecutor(executor, methodMetadata);

        //Bind builder
        B builder = builder(excelRepository, annotation, excelRepositoryTemplate, methodMetadata);
        this.bindBuilder(builder, methodMetadata);

        return annotation;
    }

    private Map<String, Object> bindMapValues(ExcelRepository excelRepository, D dslProperty, MethodMetadata methodMetadata) {
        boolean loggable = excelRepository.loggable();
        Map<String, Object> mapValues = dslProperty.transform();
        if (mapValues.containsKey(LOGGABLE)) {
            boolean repositoryLoggable = (boolean) mapValues.get(LOGGABLE);
            if (repositoryLoggable) {
                loggable = repositoryLoggable;
            }
        }
        mapValues.put(LOGGABLE, loggable);
        methodMetadata.addAttribute(ExcelContract.METHOD_LOGGABLE, loggable);
        return mapValues;
    }

    private void bindExecutor(ExcelExecutor executor, MethodMetadata methodMetadata) {
        if (Objects.nonNull(executor)) {
            methodMetadata.addAttribute(ExcelContract.METHOD_EXECUTOR, executor);
        }
    }

    private void bindBuilder(ExcelGenericBuilder<?> builder, MethodMetadata methodMetadata) {
        if (Objects.nonNull(builder)) {
            methodMetadata.addAttribute(ExcelContract.METHOD_BUILDER, builder);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<A> getAnnotationType() {
        return (Class<A>) IocUtil.getGenericSuperClass(this);
    }

    @Override
    public void setExcelRepositoryTemplate(ExcelRepositoryTemplate excelRepositoryTemplate) {
        this.excelRepositoryTemplate = excelRepositoryTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setPropertiesResolver(ExcelPropertyResolver propertiesResolver) {
        this.propertiesResolver = propertiesResolver;
    }

}
