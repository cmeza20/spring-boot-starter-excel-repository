package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class MapExcelValidated<T, E> extends ExcelValidated<T> {

    private List<E> mapList;

    public MapExcelValidated(Validated<T> validated, List<E> maps, File fileError) {
        super(validated, fileError);
        this.mapList = maps;
    }
}
