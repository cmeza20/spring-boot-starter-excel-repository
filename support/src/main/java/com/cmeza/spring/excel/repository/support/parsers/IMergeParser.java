package com.cmeza.spring.excel.repository.support.parsers;

public interface IMergeParser<C> {
    void merge(C origin, C target);
}
