package com.cmeza.spring.excel.repository.factories.excel.generics;

import com.cmeza.spring.excel.repository.support.factories.excel.StyleFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.ParentFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.StylizableFactory;
import com.cmeza.spring.excel.repository.factories.excel.StyleFactoryImpl;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class StylizableFactoryImpl<B extends StylizableFactory<?, ?>, T extends ParentFactory<?>> extends ParentFactoryImpl<T> implements StylizableFactory<B, T> {
    protected CellStyle cellStyle;
    protected String alias;
    private StyleFactory styleFactory;

    protected StylizableFactoryImpl(T parent) {
        super(parent);
    }

    @Override
    public StylizableFactory<B, T> withStyle(StyleFactory styleFactory) {
        Assert.notNull(styleFactory, "StyleFactory must not be null");
        this.styleFactory = styleFactory;
        return this;
    }

    @Override
    public StyleFactory style() {
        StyleFactory factory = new StyleFactoryImpl(this);
        withStyle(factory);
        return factory;
    }

    @Override
    public B withStyle(Consumer<StyleFactory> styleFactoryConsumer) {
        Assert.notNull(styleFactoryConsumer, "Consumer must not be null");

        StyleFactory factory = style();
        styleFactoryConsumer.accept(factory);

        return (B) this;
    }

    @Override
    public B withStyleAlias(String alias) {
        this.alias = alias;
        return (B) this;
    }

    @Override
    public String getStyleAlias() {
        return this.alias;
    }

    public void preBuilt(SXSSFWorkbook workbook, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        Assert.notNull(workbook, "SXSSFWorkbook must not be null");
        if (Objects.nonNull(styleFactory)) {
            cellStyle = ExcelUtils.build(styleFactory, workbook, interceptor, toExcelMapper, CellStyle.class);
        }
    }
}
