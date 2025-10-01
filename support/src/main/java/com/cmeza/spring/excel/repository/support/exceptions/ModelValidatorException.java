package com.cmeza.spring.excel.repository.support.exceptions;

import com.cmeza.spring.excel.repository.support.validations.ModelConstraintViolation;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ModelValidatorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final List<Exception> exceptions = new ArrayList<>();

    public ModelValidatorException() {
        super("ModelValidatorException");
    }

    public ModelValidatorException(String message) {
        super(message);
    }

    public ModelValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelValidatorException(Throwable cause) {
        super(cause);
    }

    public <T> ModelValidatorException addViolation(ModelConstraintViolation<T> violation) {
        exceptions.add(new Exception(violation.getMessage()));
        return this;
    }

    public <T> ModelValidatorException addViolation(Set<ModelConstraintViolation<T>> violations) {
        violations.forEach(violation -> exceptions.add(new Exception(violation.getMessage())));
        return this;
    }

    @Override
    public String getMessage() {
        return StringUtils.join(exceptions.stream().map(Throwable::getMessage).collect(Collectors.toList()), ", ");
    }
}
