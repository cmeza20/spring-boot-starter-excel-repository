package com.cmeza.spring.excel.repository.support.validations;

import org.springframework.util.Assert;

import java.util.Set;
import java.util.stream.Collectors;

public class ModelValidatorFactory {

    public <T> ModelConstraintViolation<T> makeConstraintViolation(jakarta.validation.ConstraintViolation<T> constraintViolation) {
        Assert.notNull(constraintViolation, "ConstraintViolation must not be null");
        return new ModelConstraintViolation<>(constraintViolation);
    }

    public <T> Set<ModelConstraintViolation<T>> makeJakartaConstraintViolation(Set<jakarta.validation.ConstraintViolation<T>> constraintViolations) {
        return constraintViolations.stream().map(this::makeConstraintViolation).collect(Collectors.toSet());
    }
}
