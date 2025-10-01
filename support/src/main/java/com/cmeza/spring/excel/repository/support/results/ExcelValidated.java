package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

@Getter
@Setter
@ToString(callSuper = true)
public class ExcelValidated<T> extends Validated<T> {

    private File fileError;

    public ExcelValidated(Validated<T> validated, File fileError) {
        super(validated);
        this.fileError = fileError;
    }
}
