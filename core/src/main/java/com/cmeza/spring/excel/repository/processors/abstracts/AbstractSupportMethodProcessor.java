package com.cmeza.spring.excel.repository.processors.abstracts;

import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import com.cmeza.spring.ioc.handler.metadata.AnnotationMetadata;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;
import com.cmeza.spring.ioc.handler.processors.AnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.utils.IocUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractSupportMethodProcessor<A extends Annotation> implements AnnotatedMethodProcessor<A>, ApplicationContextAware {
    protected ApplicationContext applicationContext;

    protected abstract void annotationProcess(ExcelRepository excelRepository, A annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata, Map<String, Object> annotationValues);

    protected abstract Object bindDefinition(A annotation, ClassMetadata classMetadata, MethodMetadata methodMetadata);

    protected abstract String tagUniqueSupport();

    @Override
    public A process(AnnotationMetadata<A> annotationMetadata, ClassMetadata classMetadata, MethodMetadata methodMetadata) {
        Assert.isTrue(!isUnique() || methodMetadata.hasAttribute(tagUniqueSupport()), "Only one annotation per method is allowed.");

        A annotation = annotationMetadata.getAnnotation();
        ExcelRepository excelRepository = classMetadata.getProcessorResult(ExcelRepository.class);

        //Annotation Process
        Map<String, Object> mapValues = AnnotationUtils.getAnnotationAttributes(annotation);
        this.annotationProcess(excelRepository, annotation, classMetadata, methodMetadata, mapValues);
        annotation = ExcelUtils.updateAnnotation(annotation, mapValues);

        Object definition = bindDefinition(annotation, classMetadata, methodMetadata);
        if (Objects.nonNull(definition)) {
            this.addAttribute(methodMetadata, definition, tagUniqueSupport());
        }

        return annotation;
    }

    protected void addAttribute(MethodMetadata methodMetadata, Object definition, String tagUniqueSupport) {
        methodMetadata.addAttribute(tagUniqueSupport, definition);
    }

    protected boolean isUnique() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<A> getAnnotationType() {
        return (Class<A>) IocUtil.getGenericSuperClass(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
