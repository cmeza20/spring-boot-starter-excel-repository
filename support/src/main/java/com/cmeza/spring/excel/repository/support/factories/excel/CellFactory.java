package com.cmeza.spring.excel.repository.support.factories.excel;


import com.cmeza.spring.excel.repository.support.factories.excel.generics.StylizableFactory;

public interface CellFactory extends StylizableFactory<CellFactory, IRowFactory<?>> {
    CellFactory withValue(Object value, String headerAlias);

    CellFactory withValue(Object value, int position);

    CellFactory withPosition(int position);

    CellFactory withFormat(String format);

    boolean appliedStyle();

    int getPosition();

    String getHeaderAlias();
}
