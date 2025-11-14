package com.cmeza.spring.excel.repository.utils;

import com.cmeza.spring.excel.repository.support.configurations.excel.FieldConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.FontConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.excel.StyleConfiguration;
import com.cmeza.spring.excel.repository.support.exceptions.InvalidReturnTypeException;
import com.cmeza.spring.excel.repository.support.exceptions.ParentNotFoundException;
import com.cmeza.spring.excel.repository.support.exceptions.WorkbookReadException;
import com.cmeza.spring.excel.repository.support.factories.excel.CellFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.StyleFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.IFactory;
import com.cmeza.spring.excel.repository.support.factories.excel.generics.ParentFactory;
import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.repositories.executors.types.ReturnType;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@UtilityClass
@SuppressWarnings("all")
public class ExcelUtils {
    private static final Integer KEEP_ROWS_MEMORY = 10; // number of rows to keep in memory (defaults to 10)
    private static final Integer BUFFER_SIZE_INPUTSTREAM = 1024; // buffer size to use when reading InputStream to file (defaults to 1024)
    private static final int MAX_CELL_LENGTH = 65280;
    private static final int MAX_CELL_CHARACTERS = 256;
    private static final double MULTIPLE_FOR_SHORT_QUANTITY = 1.5;
    private static final double MULTIPLE_FOR_LONG_QUANTITY = 1.14388;

    public String generateAlias() {
        return generateAlias(null);
    }

    public String generateAlias(String prefix) {
        if (Objects.isNull(prefix)) {
            prefix = "";
        }
        return prefix + UUID.randomUUID().toString().replace("-", "");
    }

    public Object valueNonNull(Object value) {
        return Objects.isNull(value) ? StringUtils.EMPTY : value;
    }

    public <E> void build(ParentFactory<?> factory, E entity, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper) {
        if (factory instanceof IFactory) {
            ((IFactory<?, E>) factory).build(entity, interceptor, toExcelMapper);
        }
    }

    public <E, R> R build(ParentFactory<?> factory, E entity, ToExcelInterceptor interceptor, ToExcelMapper toExcelMapper, Class<R> returnClass) {
        if (factory instanceof IFactory) {
            return ((IFactory<R, E>) factory).build(entity, interceptor, toExcelMapper);
        }
        throw new ParentNotFoundException("Unsupported Operation");
    }

    public <A extends Annotation> A updateAnnotation(A parameterAnnotation, Map<String, Object> values) {
        Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(parameterAnnotation);
        annotationAttributes.putAll(values);

        return createAnnotation(parameterAnnotation.annotationType(), annotationAttributes);
    }

    public <A extends Annotation> A createAnnotation(Class<? extends Annotation> annotationType, Map<String, Object> values) {
        return (A) AnnotationUtils.synthesizeAnnotation(
                values,
                annotationType,
                null);
    }

    public <T extends Enum<T>> T returnTypeFromEnumThrow(Class<T> enumType, ReturnType returnType) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> e.name().equals(returnType.name()))
                .findAny()
                .orElseThrow(() -> new InvalidReturnTypeException(enumType.getSimpleName() + "." + returnType.name() + " not exists"));
    }

    public Workbook readFromPath(Path path) {
        try (
                InputStream is = Files.newInputStream(path);

        ){
            Workbook workbook = new XSSFWorkbook(is);
            return workbook;
        } catch (Exception e) {
            throw new WorkbookReadException(e.getMessage(), e);
        }
    }

    public int calculateAutoSize(int value) {
        double multiply = value <= 20 ? MULTIPLE_FOR_SHORT_QUANTITY : MULTIPLE_FOR_LONG_QUANTITY;
        int width = ((int) (value * multiply)) * MAX_CELL_CHARACTERS;
        if (width > MAX_CELL_LENGTH) {
            width = MAX_CELL_LENGTH;
        }
        return width;
    }

    public void cellFactoryApply(CellFactory cellFactory, FieldConfiguration fieldConfiguration) {
        boolean apply = Objects.isNull(fieldConfiguration.getStyleRowPosition()) || IntStream.of(fieldConfiguration.getStyleRowPosition()).anyMatch(p -> p == fieldConfiguration.getRow());

        cellFactory
                .withFormat(fieldConfiguration.getDataFormat())
                .withStyleAlias(apply ? fieldConfiguration.getStyleAlias() : null);

        if (Objects.nonNull(fieldConfiguration.getPosition())) {
            cellFactory.withPosition(fieldConfiguration.getPosition());
        }

        StyleConfiguration styleConfiguration = fieldConfiguration.getStyle();

        if (Objects.nonNull(styleConfiguration) && styleConfiguration.isSet() && apply) {
            StyleFactory styleFactory = cellFactory.style();
            styleFactoryApply(styleFactory, styleConfiguration);
        }
    }

    public void styleFactoryApply(StyleFactory styleFactory, StyleConfiguration styleConfiguration) {
        styleFactory
                .withAlias(styleConfiguration.getAlias())
                .withHorizontalAlignment(styleConfiguration.getHorizontalAlignment())
                .withVerticalAlignment(styleConfiguration.getVerticalAlignment())
                .withFillPatternType(styleConfiguration.getFillPatternType())
                .withBorderStyle(styleConfiguration.getBorderStyle())
                .withBorderColor(styleConfiguration.getBorderColor())
                .withDataFormat(styleConfiguration.getDataFormat())
                .withHidden(styleConfiguration.getHidden())
                .withLocked(styleConfiguration.getLocked())
                .withQuotePrefixed(styleConfiguration.getQuotePrefixed())
                .withWrapText(styleConfiguration.getWrapText())
                .withShrinkToFit(styleConfiguration.getShrinkToFit())
                .withRotation(styleConfiguration.getRotation())
                .withIndention(styleConfiguration.getIndention())
                .withFillForegroundColor(styleConfiguration.getFillForegroundColor())
                .withFillBackgroundColor(styleConfiguration.getFillBackgroundColor());

        styleFactoryFontApply(styleFactory, styleConfiguration.getFont());
    }

    public void styleFactoryFontApply(StyleFactory styleFactory, FontConfiguration fontConfiguration) {
        if (Objects.nonNull(fontConfiguration) && fontConfiguration.isSet()) {
            XSSFFont font = new XSSFFont();
            if (Objects.nonNull(fontConfiguration.getFontHeight())) {
                font.setFontHeight(fontConfiguration.getFontHeight());
            }
            if (Objects.nonNull(fontConfiguration.getFontHeightInPoints())) {
                font.setFontHeightInPoints(fontConfiguration.getFontHeightInPoints());
            }
            if (Objects.nonNull(fontConfiguration.getItalic())) {
                font.setItalic(fontConfiguration.getItalic());
            }
            if (Objects.nonNull(fontConfiguration.getStrikeout())) {
                font.setStrikeout(fontConfiguration.getStrikeout());
            }
            if (Objects.nonNull(fontConfiguration.getBold())) {
                font.setBold(fontConfiguration.getBold());
            }
            if (Objects.nonNull(fontConfiguration.getColor())) {
                font.setColor(fontConfiguration.getColor());
            }
            if (Objects.nonNull(fontConfiguration.getTypeOffset())) {
                font.setTypeOffset(fontConfiguration.getTypeOffset());
            }
            if (Objects.nonNull(fontConfiguration.getUnderline())) {
                font.setUnderline(fontConfiguration.getUnderline());
            }
            styleFactory.withFont(font);
        }
    }

}
