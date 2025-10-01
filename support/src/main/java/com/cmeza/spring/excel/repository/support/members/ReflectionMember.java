package com.cmeza.spring.excel.repository.support.members;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.AbstractNestablePropertyAccessor;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.support.DefaultConversionService;

@Data
public abstract class ReflectionMember<T> extends BeanWrapperImpl {
    private T target;
    private Class<?> parentType;

    protected ReflectionMember(T target) {
        super(target);
        this.target = target;
        this.setConversionService(DefaultConversionService.getSharedInstance());
    }

    @Data
    @RequiredArgsConstructor
    public static class AttributeMember {
        private final String name;
        private AbstractNestablePropertyAccessor accessor;
        private PropertyTokenHolder propertyTokenHolder;
        private Class<?> attributeType;
        private boolean validatable;
        private boolean calculatedValue;
        private Object value;
    }
}
