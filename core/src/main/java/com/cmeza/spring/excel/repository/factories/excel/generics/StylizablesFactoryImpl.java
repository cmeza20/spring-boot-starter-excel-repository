package com.cmeza.spring.excel.repository.factories.excel.generics;

import com.cmeza.spring.excel.repository.support.factories.excel.StyleFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.ParentFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.StylizablesFactory;
import com.cmeza.spring.excel.repository.factories.excel.StyleFactoryImpl;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class StylizablesFactoryImpl<B extends StylizablesFactory<?, ?>, T extends ParentFactory<?>> extends ParentFactoryImpl<T> implements StylizablesFactory<B, T> {
    protected final Collection<StyleFactory> styles = new LinkedList<>();
    private Map<String, CellStyle> cellStyles = new HashMap<>();

    protected StylizablesFactoryImpl(T parent) {
        super(parent);
    }

    @Override
    public StylizablesFactory<B, T> withStyle(StyleFactory styleFactory) {
        Assert.notNull(styleFactory, "StyleFactory must not be null");
        this.styles.add(styleFactory);
        return this;
    }

    @Override
    public Optional<CellStyle> findAllStyle(String alias) {
        if (StringUtils.isEmpty(alias)) {
            return Optional.empty();
        }
        Optional<CellStyle> cellStyleOptional = getLocalStyle(alias);
        if (cellStyleOptional.isPresent()) {
            return cellStyleOptional;
        }
        return Optional.ofNullable(findStyle(parent, alias));
    }

    @Override
    public Optional<CellStyle> getLocalStyle(String alias) {
        return Optional.ofNullable(StringUtils.isEmpty(alias) ? null : cellStyles.get(alias));
    }

    @Override
    public StyleFactory style() {
        StyleFactory styleFactory = new StyleFactoryImpl(this);
        withStyle(styleFactory);
        return styleFactory;
    }

    @Override
    public B withStyle(Consumer<StyleFactory> styleFactoryConsumer) {
        Assert.notNull(styleFactoryConsumer, "Consumer must not be null");

        StyleFactory styleFactory = new StyleFactoryImpl(this);
        styleFactoryConsumer.accept(styleFactory);

        this.withStyle(styleFactory);
        return (B) this;
    }

    private CellStyle findStyle(T parent, String alias) {
        if (Objects.isNull(parent)) {
            return null;
        }
        if (parent instanceof StylizablesFactory<?, ?>) {
            Optional<CellStyle> cellStyleOptional = ((StylizablesFactory<?, ?>)parent).getLocalStyle(alias);
            return cellStyleOptional.orElseGet(() -> findStyle((T) parent.getParent(), alias));
        }

        return findStyle((T) parent.getParent(), alias);
    }

    public void preBuilt(SXSSFWorkbook workbook, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        Assert.notNull(workbook, "SXSSFWorkbook must not be null");
        cellStyles.clear();
        cellStyles = styles.stream().distinct().collect(Collectors.toMap(StyleFactory::getAlias, b -> ExcelUtils.build(b, workbook, interceptor, toExcelMapper, CellStyle.class)));
    }
}
