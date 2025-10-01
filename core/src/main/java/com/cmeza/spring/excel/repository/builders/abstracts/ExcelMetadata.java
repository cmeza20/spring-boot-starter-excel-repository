package com.cmeza.spring.excel.repository.builders.abstracts;

import org.slf4j.Logger;

import java.util.function.Supplier;

public interface ExcelMetadata {
    <R> R execute(Supplier<R> supplier, Object... params);

    void printExtras(Logger logger);
}
