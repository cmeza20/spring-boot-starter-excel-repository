package com.cmeza.spring.excel.repository.builders;

import com.cmeza.spring.excel.repository.support.maps.MapModel;
import com.cmeza.spring.excel.repository.support.results.*;

import java.io.File;
import java.util.List;

public interface ExcelToModelMapBuilder<T, M> extends ExcelToModelBuilder<T> {

    ExcelToModelMapBuilder<T, M> withMapModel(MapModel<T, M> mapModel);

    List<M> buildMap(File excelFile);

    MapValidated<T, M> buildMapValidated(File excelFile);

    MapExcelValidated<T, M> buildMapExcelValidated(File excelFile);

    MapValidatedError<T, M> buildMapValidatedError(File excelFile);

    MapExcelValidatedError<T, M> buildMapExcelValidatedError(File excelFile);

    MapViewValidated<T, M> buildMapViewValidated(File excelFile);

    MapViewValidatedError<T, M> buildMapViewValidatedError(File excelFile);

}
