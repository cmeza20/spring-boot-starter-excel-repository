package com.cmeza.spring.excel.repository.processors.classes;

import com.cmeza.spring.excel.repository.support.annotations.model.Style;
import com.cmeza.spring.excel.repository.support.annotations.model.Styles;
import com.cmeza.spring.excel.repository.contracts.ExcelContract;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.processors.supports.StyleAnnotatedMethodProcessor;
import com.cmeza.spring.ioc.handler.metadata.AnnotationMetadata;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.processors.AnnotatedClassProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class StyleAnnotatedClassProcessor implements AnnotatedClassProcessor<Style>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Style process(AnnotationMetadata<Style> annotationMetadata, ClassMetadata classMetadata) {
        List<StyleConfiguration> styleConfigurations = classMetadata.getAttribute(ExcelContract.CLASS_STYLE_CONFIGURATIONS, List.class, new ArrayList<>());
        Style annotation = annotationMetadata.getAnnotation();

        StyleConfiguration styleConfiguration = StyleAnnotatedMethodProcessor.generateConfiguration(applicationContext, annotation);

        styleConfigurations.add(styleConfiguration);

        classMetadata.addAttribute(ExcelContract.CLASS_STYLE_CONFIGURATIONS, styleConfigurations);

        return annotation;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static class ClassStyleRepeatable implements AnnotatedClassProcessor<Styles>, ApplicationContextAware{
        private ApplicationContext applicationContext;

        @Override
        public Styles process(AnnotationMetadata<Styles> annotationMetadata, ClassMetadata classMetadata) {
            List<StyleConfiguration> styleConfigurations = classMetadata.getAttribute(ExcelContract.CLASS_STYLE_CONFIGURATIONS, List.class, new ArrayList<>());
            Styles annotation = annotationMetadata.getAnnotation();

            for (Style style : annotation.value()) {
                StyleConfiguration styleConfiguration = StyleAnnotatedMethodProcessor.generateConfiguration(applicationContext, style);

                styleConfigurations.add(styleConfiguration);
            }

            classMetadata.addAttribute(ExcelContract.CLASS_STYLE_CONFIGURATIONS, styleConfigurations);

            return annotation;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }
}
