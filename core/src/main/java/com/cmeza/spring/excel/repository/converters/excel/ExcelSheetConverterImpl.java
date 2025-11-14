package com.cmeza.spring.excel.repository.converters.excel;

import com.cmeza.spring.excel.repository.support.configurations.excel.*;
import com.cmeza.spring.excel.repository.support.converters.excel.ExcelSheetConverter;
import com.cmeza.spring.excel.repository.support.converters.excel.ToExcelConverter;
import com.cmeza.spring.excel.repository.support.converters.Build;
import com.cmeza.spring.excel.repository.support.exceptions.FieldConverterException;
import com.cmeza.spring.excel.repository.support.factories.excel.*;
import com.cmeza.spring.excel.repository.support.members.EntityMember;
import com.cmeza.spring.excel.repository.utils.ModelUtils;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("all")
public class ExcelSheetConverterImpl<C extends Annotation> extends ParentConverterImpl<ToExcelConverter> implements ExcelSheetConverter, Build {
    private static final Logger log = LoggerFactory.getLogger(ExcelSheetConverterImpl.class);

    private final List<Object> data = new LinkedList<>();
    private final Map<String, FieldConfiguration> fieldConfigurations = new HashMap<>();
    private final SheetFactory sheetFactory;
    private final ProcessBuilder processBuilder;
    private boolean preBuilded;

    private Class<?> clazz;
    private SheetConfiguration sheetConfiguration;
    private Map<Class<C>, BiConsumer<C, SheetConfiguration>> sheetAnnotations = new HashMap<>();
    private Map<Class<C>, BiConsumer<C, FieldConfiguration>> fieldAnnotations = new HashMap<>();
    private List<Consumer<SheetConfiguration>> sheetConsumers = new LinkedList<>();
    private List<Consumer<FieldConfiguration>> fieldConsumers = new LinkedList<>();

    public ExcelSheetConverterImpl(ToExcelConverter parent, SheetFactory sheetFactory) {
        super(parent);
        this.sheetFactory = sheetFactory;
        this.sheetConfiguration = new SheetConfiguration();
        this.processBuilder = new ProcessBuilder();
    }

    @Override
    public ExcelSheetConverter withHeader(Consumer<HeaderConfiguration> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        consumer.accept(sheetConfiguration.getHeader());
        return this;
    }

    @Override
    public ExcelSheetConverter withTable(Consumer<TableConfiguration> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        consumer.accept(sheetConfiguration.getTable());
        return this;
    }

    @Override
    public ExcelSheetConverter withAutoSize(boolean autoSize) {
        this.sheetConfiguration.setAutoSize(autoSize);
        return this;
    }

    @Override
    public ExcelSheetConverter withSheetName(String sheetName) {
        this.sheetConfiguration.setSheetName(sheetName);
        return this;
    }

    @Override
    public <E> ExcelSheetConverter withData(Collection<E> data) {
        this.data.clear();
        this.data.addAll(data);
        return this;
    }

    @Override
    public ExcelSheetConverter preBuilt(Class<?> clazz) {
        this.clazz = clazz;
        this.prebuild(clazz);
        return this;
    }

    @Override
    public ExcelSheetConverter withSheetConfiguration(Consumer<SheetConfiguration> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        sheetConsumers.add(consumer);
        return this;
    }

    @Override
    public ExcelSheetConverter withFieldConfiguration(Consumer<FieldConfiguration> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        fieldConsumers.add(consumer);
        return this;
    }

    @Override
    public <A extends Annotation> ExcelSheetConverter withSheetAnnotation(Class<A> clazz, BiConsumer<A, SheetConfiguration> consumer) {
        Assert.notNull(clazz, "Sheet annotation must not be null");
        Assert.notNull(consumer, "Consumer must not be null");
        this.sheetAnnotations.put((Class<C>) clazz, (BiConsumer<C, SheetConfiguration>) consumer);
        return this;
    }

    @Override
    public <A extends Annotation> ExcelSheetConverter withFieldAnnotation(Class<A> clazz, BiConsumer<A, FieldConfiguration> consumer) {
        Assert.notNull(clazz, "Field annotation must not be null");
        Assert.notNull(consumer, "Consumer must not be null");
        this.fieldAnnotations.put((Class<C>) clazz, (BiConsumer<C, FieldConfiguration>) consumer);
        return this;
    }

    @Override
    public ExcelSheetConverter withMapping(String fieldName) {
        FieldConfiguration fieldConfiguration = new FieldConfiguration()
                .setFieldName(fieldName);
        this.withMapping(fieldConfiguration);
        return this;
    }

    @Override
    public ExcelSheetConverter withMapping(String fieldName, String headerName) {
        FieldConfiguration fieldConfiguration = new FieldConfiguration()
                .setFieldName(fieldName)
                .setHeaderName(headerName);
        return withMapping(fieldConfiguration);
    }

    @Override
    public ExcelSheetConverter withMapping(String fieldName, String headerName, String styleAlias) {
        FieldConfiguration fieldConfiguration = new FieldConfiguration()
                .setFieldName(fieldName)
                .setHeaderName(headerName)
                .setStyleAlias(styleAlias);
        return withMapping(fieldConfiguration);
    }

    @Override
    public ExcelSheetConverter withMapping(String fieldName, String headerName, StyleConfiguration styleConfiguration) {
        FieldConfiguration fieldConfiguration = new FieldConfiguration()
                .setFieldName(fieldName)
                .setHeaderName(headerName)
                .setStyle(styleConfiguration);
        return withMapping(fieldConfiguration);
    }

    @Override
    public ExcelSheetConverter withMapping(FieldConfiguration fieldConfiguration) {
        Assert.notNull(fieldConfiguration, "FieldConfiguration is required");
        Assert.hasLength(fieldConfiguration.getFieldName(), "Field name is required");
        this.fieldConfigurations.putIfAbsent(fieldConfiguration.getFieldName(), fieldConfiguration);
        return this;
    }

    @Override
    public void build() {
        if (!this.preBuilded) {
            prebuild();
        }
        if (!data.isEmpty()) {
            this.buildData();
        }

        this.applyAutoSize(sheetFactory);
    }

    private void prebuild() {
        this.prebuild(getClassFromData());
    }

    private void prebuild(Class<?> classData) {
        if (Objects.nonNull(classData)) {

            this.preBuilded = true;

            log.trace("Found {} class annotations", this.sheetAnnotations.size());
            log.trace("Found {} field annotations", this.fieldAnnotations.size());

            ModelUtils.configureSimpleConsumers(this.sheetConsumers, sheetConfiguration);
            ModelUtils.configureAnnotations(classData, this.sheetAnnotations, sheetConfiguration);

            this.findSheetDuplicate(sheetConfiguration);

            for (FieldConfiguration fieldConfiguration : sheetConfiguration.getMappings()) {
                withMapping(fieldConfiguration);
            }

            sheetFactory.withName(sheetConfiguration.getSheetName());
            log.trace("Building Sheet '{}'", sheetConfiguration.getSheetName());

            for (StyleConfiguration styleConfiguration : sheetConfiguration.getStyles()) {
                if (styleConfiguration.hasAlias() && styleConfiguration.isSet()) {
                    log.trace("Configure sheet style: {}", styleConfiguration.getAlias());

                    StyleFactory styleFactory = sheetFactory.style();
                    ExcelUtils.styleFactoryApply(styleFactory, styleConfiguration);
                }
            }

            HeaderConfiguration header = sheetConfiguration.getHeader();
            TableConfiguration table = sheetConfiguration.getTable();

            log.trace("With header: {}", header.isHeader());
            log.trace("With header freeze: {}", header.isFreeze());

            int row = 0;

            if (header.isHeader()) {
                HeaderFactory headerFactory = sheetFactory.header();
                headerFactory.withHeightMultiplier(header.getHeightMultiplier());
                headerFactory.withFreeze(header.isFreeze());
                sheetFactory.withTable(table.isTable());
                sheetFactory.withFilter(table.isFilter());
                sheetFactory.withTableStyle(table.getStyle());

                if (StringUtils.isNotEmpty(header.getStyleAlias())) {
                    headerFactory.withStyleAlias(header.getStyleAlias());
                }
                int[] col = {0};
                for (Field field : classData.getDeclaredFields()) {
                    this.buildField(field, headerFactory, row, col, true, (configuration, cellFactory) -> {
                        cellFactory.withValue(configuration.getHeaderName(), configuration.getHeaderName());
                        this.addAutoSizeValue(col[0], configuration.getHeaderName());
                    });
                }
                row++;
            }

            processBuilder.setRow(row);
        }
    }

    private void buildData() {
        log.trace("Writing {} rows", data.size());

        int row = processBuilder.getRow();

        for (Object obj : data) {
            RowFactory rowFactory = sheetFactory.row();

            int[] col = {0};
            for (Field field : obj.getClass().getDeclaredFields()) {
                this.buildField(field, rowFactory, row, col, false, (configuration, cellFactory) -> {
                    try {
                        String fieldName = field.getName();

                        EntityMember entityMember = new EntityMember(obj);

                        Object value = findValueFromClass(field, configuration, obj, entityMember);

                        this.addAutoSizeValue(col[0], value);

                        if (sheetConfiguration.getHeader().isHeader()) {
                            cellFactory.withValue(value, configuration.getHeaderName());
                        } else if (Objects.nonNull(configuration.getPosition())) {
                            cellFactory.withValue(value, configuration.getPosition());
                        } else {
                            cellFactory.withValue(value, col[0]);
                        }

                    } catch (Exception e) {
                        throw new FieldConverterException(field.getName(), e);
                    }
                });
            }
            row++;
        }
    }

    private void buildField(Field field, IRowFactory<?> rowFactory, int row, int[] col, boolean isHeader, BiConsumer<FieldConfiguration, CellFactory> cellFactoryConsumer) {
        try {
            String fieldName = field.getName();
            field.setAccessible(true);

            FieldConfiguration fieldConfiguration = this.findMapping(field.getName());
            fieldConfiguration.setField(field);
            fieldConfiguration.setRow(row);
            fieldConfiguration.setCol(col[0]);
            fieldConfiguration.setHeader(isHeader);

            ModelUtils.configureSimpleConsumers(this.fieldConsumers, fieldConfiguration);
            ModelUtils.configureAnnotations(field, this.fieldAnnotations, fieldConfiguration);

            col[0]++;

            if (fieldConfiguration.isIgnored()) {
                return;
            }

            StyleConfiguration styleConfiguration = fieldConfiguration.getStyle();
            if (Objects.nonNull(styleConfiguration) && styleConfiguration.isSet()) {
                log.trace("Configure Row {} Col {} style: {}", fieldConfiguration.getRow(), fieldConfiguration.getCol(), styleConfiguration.getAlias());

                StyleFactory styleFactory = rowFactory.style();
                ExcelUtils.styleFactoryApply(styleFactory, styleConfiguration);
            }

            if (Objects.isNull(fieldConfiguration.getHeaderName())) {
                fieldConfiguration.setHeaderName(field.getName());
            }

            CellFactory cellFactory = rowFactory.cell();

            if (Objects.nonNull(cellFactoryConsumer)) {
                cellFactoryConsumer.accept(fieldConfiguration, cellFactory);
            }

            ExcelUtils.cellFactoryApply(cellFactory, fieldConfiguration);
        } catch (Exception e) {
            throw new FieldConverterException(field.getName(), e);
        }
    }

    private Class<?> getClassFromData() {
        if (Objects.isNull(data) || data.isEmpty()) {
            return null;
        }
        return data.get(0).getClass();
    }

    private Object findValueFromClass(Field field, FieldConfiguration fieldConfiguration, Object origin, EntityMember originEntityMember) {
        try {
            Object value = field.get(origin);
            if (StringUtils.isEmpty(fieldConfiguration.getMapping()) || field.getName().equals(fieldConfiguration.getMapping())) {
                return ExcelUtils.valueNonNull(value);
            }

            String fieldValueName = fieldConfiguration.getMapping();

            String[] nameParts = fieldValueName.split("\\.");
            if (nameParts.length == 1) {
                return ExcelUtils.valueNonNull(this.getValue(originEntityMember, nameParts[0]));
            }

            if (!nameParts[0].equals(field.getName())) {
                throw new FieldConverterException(String.format("The attribute name must match the first item of the custom attribute - %s => %s", nameParts[0], field.getName()));
            }

            for (int i = 1; i < nameParts.length; i++) {
                if (Objects.nonNull(value)) {
                    EntityMember entityMember = new EntityMember(value);
                    value = this.getValue(entityMember, nameParts[i]);
                }
            }

            return ExcelUtils.valueNonNull(value);
        } catch (Exception e) {
            throw new FieldConverterException(field.getName(), e);
        }
    }

    private Object getValue(EntityMember entityMember, String fieldName) throws IllegalAccessException, InvocationTargetException {
        return entityMember.getValue(fieldName);
    }

    private FieldConfiguration findMapping(String fieldName) {
        return Optional.ofNullable(fieldConfigurations.get(fieldName))
                .orElse(new FieldConfiguration().setFieldName(fieldName).setMapping(fieldName));
    }

    private void addAutoSizeValue(Integer position, Object value) {
        if (sheetConfiguration.isAutoSize() && Objects.nonNull(value)) {
            Integer length = processBuilder.getAutoSizeColumns().get(position);
            if (Objects.isNull(length)) {
                processBuilder.getAutoSizeColumns().put(position, String.valueOf(value).length());
            } else if (String.valueOf(value).length() > length) {
                processBuilder.getAutoSizeColumns().put(position, String.valueOf(value).length());
            }
        }
    }

    private void applyAutoSize(SheetFactory sheetFactory) {
        if (!processBuilder.getAutoSizeColumns().isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : processBuilder.getAutoSizeColumns().entrySet()) {
                int calculated = ExcelUtils.calculateAutoSize(entry.getValue());
                sheetFactory.withColumnWidth(entry.getKey(), calculated);
            }
        }
    }

    private void findSheetDuplicate(SheetConfiguration sheetConfiguration) {
        int count = sheetFactory.getParent().sheetCount(sheetConfiguration.getSheetName());
        if (count > 0) {
            sheetConfiguration.setSheetName(sheetConfiguration.getSheetName() + count);
        }
    }

    @Data
    private static class ProcessBuilder {
        private final Map<Integer, Integer> autoSizeColumns = new LinkedHashMap<>();
        private int row;
    }
}
