package com.cmeza.spring.excel.repository.factories.excel.generics;

import com.cmeza.spring.excel.repository.support.factories.excel.generics.ParentFactory;
import com.cmeza.spring.excel.repository.support.exceptions.ParentNotFoundException;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;

import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class ParentFactoryImpl<T extends ParentFactory<?>> implements ParentFactory<T> {
    private final String id;
    protected T parent;

    protected ParentFactoryImpl(T parent) {
        this.parent = parent;
        this.id = ExcelUtils.generateAlias();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public <B> B getParent(Class<B> tClass) {
        if (Objects.isNull(parent)) {
            throw new ParentNotFoundException("Parent not exists");
        }
        return findParent(parent, tClass);
    }

    @Override
    public T getParent() {
        return parent;
    }


    private <B> B findParent(ParentFactory<?> parent, Class<?> tClass) {
        if (Objects.isNull(parent)) {
            throw new ParentNotFoundException("Parent not exists");
        }
        if (tClass.isAssignableFrom(parent.getClass())) {
            return (B) parent;
        }
        return findParent(parent.getParent(), tClass);
    }
}
