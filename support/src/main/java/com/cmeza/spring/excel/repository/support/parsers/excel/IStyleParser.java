package com.cmeza.spring.excel.repository.support.parsers.excel;

import com.cmeza.spring.excel.repository.support.parsers.IMergeParser;
import com.cmeza.spring.excel.repository.support.parsers.IParser;

import java.lang.annotation.Annotation;

public interface IStyleParser<C, A extends Annotation> extends IMergeParser<C>, IParser {
    void parse(A style, C styleConfiguration, boolean isBean);
}
