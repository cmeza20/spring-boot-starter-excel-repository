package com.cmeza.spring.excel.repository.support.extensions;

public interface ItemValueExtension<T> {
    ItemValueExtension<T> withValue(T value);

    ItemValueExtension<T> withError(boolean error);

    T getValue();

    boolean isError();
}
