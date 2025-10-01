package com.cmeza.spring.excel.repository.factories;

import com.cmeza.spring.excel.repository.factories.excel.WorkbookFactoryImpl;
import com.cmeza.spring.excel.repository.factories.model.ModelFactoryImpl;
import com.cmeza.spring.excel.repository.support.factories.excel.WorkbookFactory;
import com.cmeza.spring.excel.repository.support.factories.model.ModelFactory;
import com.cmeza.spring.excel.repository.support.factories.model.ModelMapFactory;

public interface Factory {
    static <T> ModelFactory<T> getModelFactory(Class<T> modelClass) {
        return getModelFactory(modelClass, null);
    }

    static <T, U> ModelMapFactory<T, U> getModelFactory(Class<T> modelClass, Class<U> mapClass) {
        return new ModelFactoryImpl<>(modelClass, mapClass);
    }

    static WorkbookFactory getWorkbookFactory() {
        return new WorkbookFactoryImpl();
    }
}
