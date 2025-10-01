package com.cmeza.spring.excel.repository.support.results;

import com.cmeza.spring.excel.repository.support.extensions.ModelValidatorExtension;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ValidatedError<T> {
    private final List<T> all;
    private final List<T> errors;

    public ValidatedError(ModelValidatorExtension<T> modelValidatorExtension, List<T> all) {
        this.all = all;
        this.errors = modelValidatorExtension.getErrors();
    }

    public ValidatedError(ValidatedError<T> validatedError) {
        this.all = validatedError.getAll();
        this.errors = validatedError.getErrors();
    }
}
