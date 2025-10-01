package com.cmeza.spring.excel.repository.converters.excel;

import com.cmeza.spring.excel.repository.support.converters.excel.ParentConverter;
import org.springframework.util.Assert;

public abstract class ParentConverterImpl<T> implements ParentConverter {
    protected final T parent;

    protected ParentConverterImpl(T parent) {
        Assert.notNull(parent, "Parent must not be null");
        this.parent = parent;
    }
}
