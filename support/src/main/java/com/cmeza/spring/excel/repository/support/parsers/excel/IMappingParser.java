package com.cmeza.spring.excel.repository.support.parsers.excel;

import com.cmeza.spring.excel.repository.support.parsers.IMergeParser;
import com.cmeza.spring.excel.repository.support.parsers.IParser;
import com.cmeza.spring.excel.repository.support.parsers.ITransformParser;

import java.lang.annotation.Annotation;

public interface IMappingParser<C, M, A extends Annotation, D> extends ITransformParser<A, C>, IMergeParser<C>, IParser {
    void parseModel(A mapping, M modelConfiguration);

    void parseDsl(A mapping, D dsl);

    void mergeModel(M origin, M target);
}
