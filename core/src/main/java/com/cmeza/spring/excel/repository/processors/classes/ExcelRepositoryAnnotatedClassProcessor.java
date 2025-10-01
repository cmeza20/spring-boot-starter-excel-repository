package com.cmeza.spring.excel.repository.processors.classes;

import com.cmeza.spring.excel.repository.support.annotations.ExcelRepository;
import com.cmeza.spring.ioc.handler.metadata.AnnotationMetadata;
import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.processors.AnnotatedClassProcessor;

public class ExcelRepositoryAnnotatedClassProcessor implements AnnotatedClassProcessor<ExcelRepository> {

    @Override
    public ExcelRepository process(AnnotationMetadata<ExcelRepository> annotationMetadata, ClassMetadata classMetadata) {
        return annotationMetadata.getAnnotation();
    }
}
