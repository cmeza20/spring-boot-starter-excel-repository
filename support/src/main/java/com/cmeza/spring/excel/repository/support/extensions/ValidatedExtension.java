package com.cmeza.spring.excel.repository.support.extensions;

import com.cmeza.spring.excel.repository.support.results.*;

import java.io.File;
import java.nio.file.Path;

public interface ValidatedExtension<T> {

    //All
    Validated<T> buildValidated(File excelFile);

    ViewValidated<T> buildViewValidated(File excelFile);

    ExcelValidated<T> buildExcelValidated(File excelFile);


    //Only Errors
    ValidatedError<T> buildValidatedError(File excelFile);

    ExcelValidatedError<T> buildExcelValidatedError(File excelFile);

    ViewValidatedError<T> buildViewValidatedError(File excelFile);

    ValidatedExtension<T> withErrorFile(boolean errorFile);

    ValidatedExtension<T> withErrorFolder(Path folder);

    ValidatedExtension<T> withErrorFileName(String fileName);

    ValidatedExtension<T> withErrorVersioned(boolean versioned);

    //Validators
    ValidatedExtension<T> withValidator(jakarta.validation.Validator validator);

}
