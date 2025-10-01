package com.cmeza.spring.excel.repository.factories.model;

import com.cmeza.spring.excel.repository.support.extensions.ItemErrorExtension;
import com.cmeza.spring.excel.repository.support.extensions.ItemValueExtension;
import com.cmeza.spring.excel.repository.support.factories.model.ItemFactory;
import com.cmeza.spring.excel.repository.support.members.ValueObject;
import com.cmeza.spring.excel.repository.utils.ModelUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemFactoryImpl<T> implements ItemFactory<T>, ItemValueExtension<T>, ItemErrorExtension<T> {

    private final Map<String, ValueObject> values;
    private final List<Exception> errors;
    private Function<List<Exception>, String> errorGrouper;
    private Consumer<ItemErrorExtension<T>> consumerListener;
    private T value;
    private boolean error;
    private int row;

    public ItemFactoryImpl() {
        this.values = new HashMap<>();
        this.errors = new ArrayList<>();
    }

    @Override
    public ItemFactory<T> withValue(String attribute, Object value) {
        Assert.hasLength(attribute, "attribute must not be empty");
        Assert.notNull(value, "value must not null");
        values.put(attribute, new ValueObject(value, ModelUtils.isHierarchical(attribute), null));
        return this;
    }

    @Override
    public ItemFactory<T> withValue(String attribute, Object value, Class<?> castClass) {
        Assert.notNull(value, "castClass must not null");
        values.put(attribute, new ValueObject(value, ModelUtils.isHierarchical(attribute), castClass));
        return this;
    }

    @Override
    public Map<String, ValueObject> build() {
        return values;
    }

    @Override
    public ItemErrorExtension<T> withError(Exception modelItemException) {
        this.errors.add(modelItemException);
        return this;
    }

    @Override
    public ItemErrorExtension<T> withErrorGrouper(Function<List<Exception>, String> exceptionGrouper) {
        Assert.notNull(exceptionGrouper, "Function must not be null");
        this.errorGrouper = exceptionGrouper;
        return this;
    }

    @Override
    public ItemErrorExtension<T> withErrorListener(Consumer<ItemErrorExtension<T>> consumerListener) {
        Assert.notNull(consumerListener, "ConsumerListener must not be null");
        this.consumerListener = consumerListener;
        return this;
    }

    @Override
    public ItemErrorExtension<T> consumeErrorListener(ItemErrorExtension<T> itemErrorExtension) {
        if (Objects.nonNull(consumerListener)) {
            consumerListener.accept(itemErrorExtension);
        }
        return this;
    }

    @Override
    public ItemErrorExtension<T> withRow(int row) {
        this.row = row;
        return this;
    }

    @Override
    public List<Exception> getErrors() {
        return this.errors;
    }

    @Override
    public String getGroupedErrors() {
        if (Objects.isNull(this.errorGrouper)) {
            this.errorGrouper = list -> list.stream()
                    .map(Throwable::getMessage)
                    .collect(Collectors.joining(", "));
        }
        return this.errorGrouper.apply(this.errors);
    }

    @Override
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override
    public int getRow() {
        return this.row;
    }

    @Override
    public ItemValueExtension<T> withValue(T value) {
        this.value = value;
        return this;
    }

    @Override
    public ItemValueExtension<T> withError(boolean error) {
        this.error = error;
        return this;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public boolean isError() {
        return this.error;
    }
}
