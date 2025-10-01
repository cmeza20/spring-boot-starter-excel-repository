package com.cmeza.spring.excel.repository.support.parsers.model;

import com.cmeza.spring.excel.repository.support.parsers.IMergeParser;
import com.cmeza.spring.excel.repository.support.parsers.IParser;
import com.cmeza.spring.excel.repository.support.parsers.ITransformParser;

import java.lang.annotation.Annotation;

public interface IErrorParser<C, A extends Annotation, D> extends ITransformParser<A, C>, IMergeParser<C>, IParser {
    void parseDsl(A error, D dsl);
}
