package com.cmeza.spring.excel.repository.support.parsers;

import java.lang.annotation.Annotation;

public interface ITransformParser<A extends Annotation, C> {
    void parse(A annotation, C configuration);
}
