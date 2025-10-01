package com.cmeza.spring.excel.repository.support.extensions;


import com.cmeza.spring.excel.repository.support.members.EntityMember;
import com.cmeza.spring.excel.repository.support.parsers.values.ValueParser;

import java.util.Objects;

public interface FactoryMapperExtension<T> {
    default void mapFactoryValue(EntityMember<?> entityMember, String modelAttribute, Object value, Class<?> classCast, ValueParser<?> valueParser) {
        if (Objects.isNull(classCast)) {
            entityMember.setValue(modelAttribute, value);
        } else {
            entityMember.setValue(modelAttribute, Objects.nonNull(valueParser) ? valueParser.getValue(value) : classCast.cast(value));
        }
    }

    default T afterFactoryMap(EntityMember<T> entityMember) {
        return entityMember.getTarget();
    }
}
