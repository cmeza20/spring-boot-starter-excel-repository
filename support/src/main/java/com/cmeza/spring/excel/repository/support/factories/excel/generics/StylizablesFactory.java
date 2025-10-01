package com.cmeza.spring.excel.repository.support.factories.excel.generics;

import com.cmeza.spring.excel.repository.support.factories.excel.StyleFactory;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.Optional;
import java.util.function.Consumer;

public interface StylizablesFactory<B extends StylizablesFactory<?, ?>, T extends ParentFactory<?>> extends ParentFactory<T> {

    StylizablesFactory<B, T> withStyle(StyleFactory styleFactory);

    StyleFactory style();

    B withStyle(Consumer<StyleFactory> styleFactoryConsumer);

    Optional<CellStyle> findAllStyle(String alias);

    Optional<CellStyle> getLocalStyle(String alias);

}
