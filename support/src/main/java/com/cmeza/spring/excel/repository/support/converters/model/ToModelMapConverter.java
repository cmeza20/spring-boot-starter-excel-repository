package com.cmeza.spring.excel.repository.support.converters.model;

import com.cmeza.spring.excel.repository.support.maps.MapModel;
import com.cmeza.spring.excel.repository.support.results.*;

import java.io.File;
import java.util.List;

public interface ToModelMapConverter<T, U> extends ToModelConverter<T> {

    ToModelMapConverter<T, U> withMapModel(MapModel<T, U> mapModel);

    List<U> buildMap(File excelFile);

    //All
    MapValidated<T, U> buildMapValidated(File excelFile);

    MapExcelValidated<T, U> buildMapExcelValidated(File excelFile);

    MapViewValidated<T, U> buildMapViewValidated(File excelFile);

    //Only error
    MapValidatedError<T, U> buildMapValidatedError(File excelFile);

    MapExcelValidatedError<T, U> buildMapExcelValidatedError(File excelFile);

    MapViewValidatedError<T, U> buildMapViewValidatedError(File excelFile);
}
