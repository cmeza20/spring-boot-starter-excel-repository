package com.cmeza.spring.excel.repository.support.factories.excel.generics;

public interface ParentFactory<T extends ParentFactory<?>> {
    String getId();

    T getParent();

    <B> B getParent(Class<B> tClass);
}
