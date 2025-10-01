package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class MapExcelValidatedError<T, E> extends ExcelValidatedError<T> {

    private List<E> mapList;

    public MapExcelValidatedError(ValidatedError<T> validatedError, List<E> maps, File fileError) {
        super(validatedError, fileError);
        this.mapList = maps;
    }
}
