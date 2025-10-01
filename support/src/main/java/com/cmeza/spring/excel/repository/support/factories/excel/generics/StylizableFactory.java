package com.cmeza.spring.excel.repository.support.factories.excel.generics;


import com.cmeza.spring.excel.repository.support.factories.excel.StyleFactory;

import java.util.function.Consumer;

public interface StylizableFactory<B extends StylizableFactory<?, ?>, T extends ParentFactory<?>> extends ParentFactory<T> {

    B withStyleAlias(String alias);

    StylizableFactory<B, T> withStyle(StyleFactory styleFactory);

    StyleFactory style();

    B withStyle(Consumer<StyleFactory> styleFactoryConsumer);

    String getStyleAlias();
}
