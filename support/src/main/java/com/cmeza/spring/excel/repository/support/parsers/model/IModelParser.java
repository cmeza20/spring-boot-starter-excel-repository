package com.cmeza.spring.excel.repository.support.parsers.model;

import com.cmeza.spring.excel.repository.support.parsers.IParser;

import java.lang.annotation.Annotation;

public interface IModelParser<C, A extends Annotation, D> extends IParser {
    void parse(A annotation, C modelConfiguration);

    void parseDsl(A annotation, D dsl);

    void merge(C origin, C target);
}
