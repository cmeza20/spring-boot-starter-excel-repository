package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

@Getter
@Setter
@ToString(callSuper = true)
public class ExcelValidatedError<T> extends ValidatedError<T> {

    private File fileError;

    public ExcelValidatedError(ValidatedError<T> validatedError, File fileError) {
        super(validatedError);
        this.fileError = fileError;
    }
}
