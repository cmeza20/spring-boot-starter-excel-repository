package com.cmeza.spring.excel.repository.support.parsers.excel;

import com.cmeza.spring.excel.repository.support.parsers.ITransformParser;
import com.cmeza.spring.excel.repository.support.parsers.IParser;
import com.cmeza.spring.excel.repository.support.parsers.IMergeParser;

import java.lang.annotation.Annotation;

public interface IExcelParser<C, A extends Annotation, D> extends ITransformParser<A, C>, IMergeParser<C>, IParser {
    void parseDsl(A annotation, D dsl);
}
