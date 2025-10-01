package com.cmeza.spring.excel.repository.support.validations;

import lombok.Getter;

@Getter
public class ModelConstraintViolation<T> {
    private final String message;
    private final T rootBean;
    private final Class<T> rootBeanClass;
    private final Object invalidValue;

    public ModelConstraintViolation(javax.validation.ConstraintViolation<T> constraintViolation) {
        this.message = constraintViolation.getMessage();
        this.rootBean = constraintViolation.getRootBean();
        this.rootBeanClass = constraintViolation.getRootBeanClass();
        this.invalidValue = constraintViolation.getInvalidValue();
    }

    public ModelConstraintViolation(jakarta.validation.ConstraintViolation<T> constraintViolation) {
        this.message = constraintViolation.getMessage();
        this.rootBean = constraintViolation.getRootBean();
        this.rootBeanClass = constraintViolation.getRootBeanClass();
        this.invalidValue = constraintViolation.getInvalidValue();
    }
}
