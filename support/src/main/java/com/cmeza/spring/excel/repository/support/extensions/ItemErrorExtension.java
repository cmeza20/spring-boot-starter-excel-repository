package com.cmeza.spring.excel.repository.support.extensions;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ItemErrorExtension<T> {
    ItemErrorExtension<T> withError(Exception exception);

    ItemErrorExtension<T> withErrorGrouper(Function<List<Exception>, String> modelItemExceptionStringFunction);

    ItemErrorExtension<T> withErrorListener(Consumer<ItemErrorExtension<T>> consumerListener);

    ItemErrorExtension<T> consumeErrorListener(ItemErrorExtension<T> itemErrorExtension);

    ItemErrorExtension<T> withRow(int row);

    List<Exception> getErrors();

    String getGroupedErrors();

    boolean hasErrors();

    int getRow();
}
