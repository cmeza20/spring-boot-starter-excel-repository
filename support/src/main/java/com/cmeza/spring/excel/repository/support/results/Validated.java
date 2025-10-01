package com.cmeza.spring.excel.repository.support.results;

import com.cmeza.spring.excel.repository.support.extensions.ModelValidatorExtension;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class Validated<T> {
    private final List<T> all;
    private final List<T> successful;
    private final List<T> errors;

    public Validated(ModelValidatorExtension<T> modelValidatorExtension, List<T> all) {
        this.all = all;
        this.successful = modelValidatorExtension.getSuccessful();
        this.errors = modelValidatorExtension.getErrors();
    }

    public Validated(Validated<T> validated) {
        this.all = validated.getAll();
        this.successful = validated.getSuccessful();
        this.errors = validated.getErrors();
    }
}
