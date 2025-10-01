package com.cmeza.spring.excel.repository.support.parsers.excel;

import com.cmeza.spring.excel.repository.support.parsers.IMergeParser;
import com.cmeza.spring.excel.repository.support.parsers.IParser;
import com.cmeza.spring.excel.repository.support.parsers.ITransformParser;

import java.lang.annotation.Annotation;

public interface ITableParser<C, A extends Annotation> extends ITransformParser<A, C>, IMergeParser<C>, IParser {
}
