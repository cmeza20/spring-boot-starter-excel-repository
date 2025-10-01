package com.cmeza.spring.excel.repository.support.extensions;

import com.cmeza.spring.excel.repository.support.factories.model.ModelFactory;

import java.util.List;

public interface ModelValidatorExtension<T> extends ModelFactory<T> {
    boolean isSaveSuccessful();

    boolean isSaveErrors();

    ModelValidatorExtension<T> withSaveErrors(boolean saveErrors);

    ModelValidatorExtension<T> withSaveSuccessful(boolean saveSuccessful);

    ModelValidatorExtension<T> withValidator(javax.validation.Validator validator);

    ModelValidatorExtension<T> withValidator(jakarta.validation.Validator validator);

    List<T> getSuccessful();

    List<T> getErrors();

    boolean hasErrors();
}
