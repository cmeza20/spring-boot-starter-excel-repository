package com.cmeza.spring.excel.repository.support.factories.model;

import com.cmeza.spring.excel.repository.support.extensions.ModelValidatorExtension;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;

import java.util.List;
import java.util.function.Consumer;

public interface ModelFactory<T> {

    ModelFactory<T> withHierarchical(boolean hierarchical);

    ModelFactory<T> withModelMapper(ToModelMapper<T> mapper);

    ItemFactory<T> item();

    ModelFactory<T> withItem(Consumer<ItemFactory<T>> itemModelFactoryConsumer);

    ToModelMapper<T> getModelMapper();

    Class<T> getModelClass();

    List<T> build();

    void preBuilt();

    ModelValidatorExtension<T> toValidator();
}
