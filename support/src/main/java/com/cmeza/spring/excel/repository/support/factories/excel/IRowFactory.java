package com.cmeza.spring.excel.repository.support.factories.excel;



import com.cmeza.spring.excel.repository.support.factories.excel.generics.StylizableFactory;

import java.util.function.Consumer;

public interface IRowFactory<T extends StylizableFactory<?, ?>> extends StylizableFactory<T, SheetFactory> {

    T withCell(Object value);

    T withCell(Object value, String headerAlias);

    T withCell(Object value, int position);

    CellFactory cell();

    T withCell(Consumer<CellFactory> cellFactoryConsumer);

    T withHeightMultiplier(int multiplier);

}
