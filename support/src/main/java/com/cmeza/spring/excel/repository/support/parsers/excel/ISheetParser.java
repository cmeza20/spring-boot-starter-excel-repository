package com.cmeza.spring.excel.repository.support.parsers.excel;

import com.cmeza.spring.excel.repository.support.parsers.IMergeParser;
import com.cmeza.spring.excel.repository.support.parsers.IParser;

import java.lang.annotation.Annotation;

public interface ISheetParser<C, A extends Annotation> extends IMergeParser<C>, IParser {
    void parse(A annotation, C sheetConfiguration, boolean isBean);
}
