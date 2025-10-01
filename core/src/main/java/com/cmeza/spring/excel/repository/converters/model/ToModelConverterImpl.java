package com.cmeza.spring.excel.repository.converters.model;

import com.cmeza.spring.excel.repository.support.converters.model.ToModelConverter;
import com.cmeza.spring.excel.repository.support.converters.model.ToModelMapConverter;
import com.cmeza.spring.excel.repository.factories.Factory;
import com.cmeza.spring.excel.repository.parsers.model.ModelParser;
import com.cmeza.spring.excel.repository.support.configurations.model.AttributeConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.model.ModelConfiguration;
import com.cmeza.spring.excel.repository.support.extensions.ItemErrorExtension;
import com.cmeza.spring.excel.repository.support.extensions.ModelValidatorExtension;
import com.cmeza.spring.excel.repository.support.extensions.ValidatedExtension;
import com.cmeza.spring.excel.repository.support.exceptions.ExcelException;
import com.cmeza.spring.excel.repository.support.exceptions.ModelException;
import com.cmeza.spring.excel.repository.support.factories.model.ItemFactory;
import com.cmeza.spring.excel.repository.support.factories.model.ModelFactory;
import com.cmeza.spring.excel.repository.support.factories.model.ModelMapFactory;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.results.*;
import com.cmeza.spring.excel.repository.support.utils.SupportUtils;
import com.cmeza.spring.excel.repository.utils.ExcelUtils;
import com.cmeza.spring.excel.repository.utils.ModelUtils;
import com.cmeza.spring.excel.repository.utils.ReflectionUtils;
import com.github.pjfanning.xlsx.StreamingReader;
import com.github.pjfanning.xlsx.exceptions.MissingSheetException;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.Assert;
import org.springframework.web.servlet.View;

import javax.validation.Validator;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("all")
public class ToModelConverterImpl<T, C extends Annotation, M> implements ToModelMapConverter<T, M>, ValidatedExtension<T> {

    private static final Parser PARSER = Parser.getInstance();
//    private final ModelFactory<T> modelFactory;
//    private final ModelValidatorExtension<T> modelValidatorExtension;
//    private final ModelMapFactory<T, M> modelMapFactory;
    private final ModelConfiguration<T> modelConfiguration;

    private final Map<Class<C>, BiConsumer<C, ModelConfiguration<T>>> modelAnnotations = new HashMap<>();
    private final Map<Class<C>, BiConsumer<C, AttributeConfiguration>> attributeAnnotations = new HashMap<>();
    private final List<Consumer<ModelConfiguration<T>>> modelConsumers = new LinkedList<>();
    private final List<Consumer<AttributeConfiguration>> attributeConsumers = new LinkedList<>();
    private final List<ItemErrorExtension<T>> errorList = new LinkedList<>();
    private final Consumer<ItemErrorExtension<T>> consumerListener;
    private final WritableConfiguration writableConfiguration = new WritableConfiguration();
    private final Map<Integer, Integer> autoSizeColumns = new LinkedHashMap<>();
    private final Class<T> modelClass;
    private final Class<M> mapClass;

    private boolean preBuilt;
    private boolean columnsBuilded;
    private MapModel<T, M> mapModel;
    private ToModelMapper<T> mapper;
    private Validator validator;
    private jakarta.validation.Validator jakartaValidator;

    public ToModelConverterImpl(Class<T> modelClass) {
        this(modelClass, null);
    }

    public ToModelConverterImpl(Class<T> modelClass, Class<M> mapClass) {
        this.modelClass = modelClass;
        this.mapClass = mapClass;
        this.modelConfiguration = new ModelConfiguration<>();
        this.consumerListener = itemErrorExtension -> {
            errorList.add(itemErrorExtension);
        };
    }

    @Override
    public ToModelConverter<T> withModelConfiguration(ModelConfiguration<T> modelConfiguration) {
        Assert.notNull(modelConfiguration, "ModelConfiguration must not be null");
        PARSER.getParser(ModelParser.class).merge(modelConfiguration, this.modelConfiguration);
        return this;
    }

    @Override
    public ToModelConverter<T> withModelConfiguration(Consumer<ModelConfiguration<T>> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        modelConsumers.add(consumer);
        return this;
    }

    @Override
    public ToModelConverter<T> withAttributeConfiguration(Consumer<AttributeConfiguration> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        attributeConsumers.add(consumer);
        return this;
    }

    @Override
    public <A extends Annotation> ToModelConverter<T> withModelAnnotation(Class<A> clazz, BiConsumer<A, ModelConfiguration<T>> consumer) {
        Assert.notNull(clazz, "Model annotation must not be null");
        Assert.notNull(consumer, "Consumer must not be null");
        this.modelAnnotations.put((Class<C>) clazz, (BiConsumer<C, ModelConfiguration<T>>) consumer);
        return this;
    }

    @Override
    public <A extends Annotation> ToModelConverter<T> withAttributeAnnotation(Class<A> clazz, BiConsumer<A, AttributeConfiguration> consumer) {
        Assert.notNull(clazz, "Attribute annotation must not be null");
        Assert.notNull(consumer, "Consumer must not be null");
        this.attributeAnnotations.put((Class<C>) clazz, (BiConsumer<C, AttributeConfiguration>) consumer);
        return this;
    }

    @Override
    public ToModelConverter<T> withModelMapper(ToModelMapper<T> mapper) {
        Assert.notNull(mapper, "Model mapper must not be null");
        this.mapper = mapper;
        return this;
    }

    @Override
    public ToModelMapConverter<T, M> withMapModel(MapModel<T, M> mapModel) {
        this.mapModel = mapModel;
        return this;
    }

    @Override
    public ToModelConverter<T> withSheetIndex(int sheetIndex) {
        Assert.isTrue(sheetIndex > 0, "Sheet index must be greater than 0");
        this.modelConfiguration.setSheetIndex(sheetIndex);
        return this;
    }

    @Override
    public ToModelConverter<T> withSheetName(String sheetName) {
        Assert.hasText(sheetName, "Sheet name must not be null or empty");
        this.modelConfiguration.setSheetName(sheetName);
        return this;
    }

    @Override
    public ToModelConverter<T> withRowCacheSize(int rowCacheSize) {
        Assert.isTrue(rowCacheSize > 0, "RowCacheSize must be greater than 0");
        this.modelConfiguration.setRowCacheSize(rowCacheSize);
        return this;
    }

    @Override
    public ToModelConverter<T> withBufferSize(int bufferSize) {
        Assert.isTrue(bufferSize > 0, "BufferSize must be greater than 0");
        this.modelConfiguration.setBufferSize(bufferSize);
        return this;
    }

    @Override
    public ValidatedExtension<T> withErrorFile(boolean errorFile) {
        this.writableConfiguration.setErrorFile(errorFile);
        return this;
    }

    @Override
    public ValidatedExtension<T> withErrorFolder(Path folder) {
        this.writableConfiguration.setErrorFolder(folder);
        return this;
    }

    @Override
    public ValidatedExtension<T> withErrorFileName(String fileName) {
        this.writableConfiguration.setFileName(fileName);
        return this;
    }

    @Override
    public ValidatedExtension<T> withErrorVersioned(boolean versioned) {
        this.writableConfiguration.setVersioned(versioned);
        return this;
    }

    @Override
    public ValidatedExtension<T> withValidator(Validator validator) {
        this.validator = validator;
        return this;
    }

    @Override
    public ValidatedExtension<T> withValidator(jakarta.validation.Validator validator) {
        this.jakartaValidator = validator;
        return this;
    }

    @Override
    public ToModelConverter<T> withMapping(String fieldName) {
        this.modelConfiguration.addMapping(fieldName);
        return this;
    }

    @Override
    public ToModelConverter<T> withMapping(String fieldName, String headerName) {
        this.modelConfiguration.addMapping(fieldName, headerName);
        return this;
    }

    @Override
    public ToModelConverter<T> withMapping(String fieldName, String headerName, Class<?> fieldType) {
        this.modelConfiguration.addMapping(fieldName, headerName, fieldType);
        return this;
    }

    @Override
    public ToModelConverter<T> withMapping(AttributeConfiguration attributeConfiguration) {
        this.modelConfiguration.addMapping(attributeConfiguration);
        return this;
    }

    @Override
    public List<T> build(File excelFile) {
        Assert.notNull(excelFile, "File must not be null");
        ModelFactory<T> modelFactory = Factory.getModelFactory(modelClass, mapClass);
        this.populateModelFactory(modelFactory);
        this.internalBuild(excelFile, modelFactory);
        return this.buildFactory(modelFactory);
    }

    @Override
    public List<M> buildMap(File excelFile) {
        Assert.notNull(excelFile, "File must not be null");
        ModelFactory<T> modelFactory = Factory.getModelFactory(modelClass, mapClass);
        this.populateModelFactory(modelFactory);
        this.internalBuild(excelFile, modelFactory);
        this.buildFactory(modelFactory);
        return ModelUtils.convertTo(modelFactory, ModelMapFactory.class).buildMap();
    }

    @Override
    public MapValidated<T, M> buildMapValidated(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedFactory(excelFile);
        return new MapValidated(validatedFactory.getValidated(), validatedFactory.getModelMapFactory().buildMap());
    }

    @Override
    public MapExcelValidated<T, M> buildMapExcelValidated(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedFactory(excelFile);
        Validated<T> validated = validatedFactory.getValidated();
        return new MapExcelValidated(validated, validatedFactory.getModelMapFactory().buildMap(), writableConfiguration.executeFile(!validated.getErrors().isEmpty()));
    }

    @Override
    public MapViewValidated<T, M> buildMapViewValidated(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedFactory(excelFile);
        Validated<T> validated = validatedFactory.getValidated();
        return new MapViewValidated(validated, validatedFactory.getModelMapFactory().buildMap(), writableConfiguration.executeView(!validated.getErrors().isEmpty()));
    }

    @Override
    public MapValidatedError<T, M> buildMapValidatedError(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedFactory(excelFile);
        ValidatedError<T> validated = validatedFactory.getValidatedError();
        return new MapValidatedError<>(validated, validatedFactory.getModelMapFactory().buildMap());
    }

    @Override
    public MapExcelValidatedError<T, M> buildMapExcelValidatedError(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedFactory(excelFile);
        ValidatedError<T> validated = validatedFactory.getValidatedError();
        return new MapExcelValidatedError<>(validated, validatedFactory.getModelMapFactory().buildMap(), writableConfiguration.executeFile(!validated.getErrors().isEmpty()));
    }

    @Override
    public MapViewValidatedError<T, M> buildMapViewValidatedError(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedFactory(excelFile);
        ValidatedError<T> validated = validatedFactory.getValidatedError();
        return new MapViewValidatedError<>(validated, validatedFactory.getModelMapFactory().buildMap(), writableConfiguration.executeView(!validated.getErrors().isEmpty()));
    }

    @Override
    public Validated<T> buildValidated(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedFactory(excelFile);
        return new Validated(validatedFactory.getModelValidatorExtension(), validatedFactory.getValidated().getAll());
    }

    @Override
    public ViewValidated<T> buildViewValidated(File excelFile) {
        Validated<T> validated = this.buildValidated(excelFile);
        return new ViewValidated(validated, writableConfiguration.executeView(!validated.getErrors().isEmpty()));
    }

    @Override
    public ExcelValidated<T> buildExcelValidated(File excelFile) {
        Validated<T> validated = this.buildValidated(excelFile);
        return new ExcelValidated<>(validated, writableConfiguration.executeFile(!validated.getErrors().isEmpty()));
    }

    @Override
    public ValidatedError<T> buildValidatedError(File excelFile) {
        ValidatedFactory<T, M> validatedFactory = this.buildValidatedErrorFactory(excelFile);
        return new ValidatedError(validatedFactory.getModelValidatorExtension(), validatedFactory.getValidatedError().getAll());
    }

    @Override
    public ExcelValidatedError<T> buildExcelValidatedError(File excelFile) {
        ValidatedError<T> validated = this.buildValidatedError(excelFile);
        return new ExcelValidatedError<>(validated, writableConfiguration.executeFile(!validated.getErrors().isEmpty()));
    }

    @Override
    public ViewValidatedError<T> buildViewValidatedError(File excelFile) {
        ValidatedError<T> validated = this.buildValidatedError(excelFile);
        return new ViewValidatedError(validated, writableConfiguration.executeView(!validated.getErrors().isEmpty()));
    }

    @Override
    public void preBuild(ModelFactory<T> modelFactory) {
        if (!preBuilt) {
            preBuilt = true;

            Class<T> modelClass = modelFactory.getModelClass();

            modelConsumers.forEach(consumer -> consumer.accept(modelConfiguration));

            log.trace("Found {} class annotations", this.modelAnnotations.size());
            log.trace("Found {} attributes annotations", this.attributeAnnotations.size());

            ModelUtils.configureSimpleConsumers(this.modelConsumers, modelConfiguration);
            ModelUtils.configureAnnotations(modelClass, this.modelAnnotations, modelConfiguration);

            modelFactory.withHierarchical(modelConfiguration.isHierarchical());

            if (!modelConfiguration.isHierarchical()) {
                this.cleanHierarchical();
            }

            boolean hasAnnotations = ReflectionUtils.hasFieldAnnotation(modelClass, attributeAnnotations);

            for (Field field : modelClass.getDeclaredFields()) {
                String fieldName = field.getName();

                AttributeConfiguration attributeConfiguration = new AttributeConfiguration(fieldName);
                attributeConfiguration.setReflectionMapping(true);

                this.attributesApply(field, attributeConfiguration);

                boolean isHierarchical = ModelUtils.isHierarchical(attributeConfiguration.getFieldName());
                this.bindFieldType(modelClass, attributeConfiguration.getFieldName(), attributeConfiguration, isHierarchical);
                this.modelConfiguration.addMapping(attributeConfiguration);
            }

            for (AttributeConfiguration attributeConfiguration : modelConfiguration.getMappings().values()) {
                try {
                    if (!attributeConfiguration.isReflectionMapping()) {
                        boolean isHierarchical = ModelUtils.isHierarchical(attributeConfiguration.getFieldName());

                        if (!isHierarchical) {
                            String part = attributeConfiguration.getFieldName().split("\\.")[0];
                            Field field = modelClass.getDeclaredField(part);
                            this.attributesApply(field, attributeConfiguration);
                        }

                        this.bindFieldType(modelClass, attributeConfiguration.getFieldName(), attributeConfiguration, isHierarchical);
                    }
                } catch (Exception e) {
                    throw new ModelException(e);
                }
            }
        }
    }

    private void attributesApply(Field field, AttributeConfiguration attributeConfiguration) {
        ModelUtils.configureSimpleConsumers(this.attributeConsumers, attributeConfiguration);
        ModelUtils.configureAnnotations(field, this.attributeAnnotations, attributeConfiguration);
    }

    @Override
    public ModelConfiguration<T> getConfiguration() {
        return modelConfiguration;
    }

    @Override
    public ValidatedExtension<T> toValidated() {
        return this;
    }

    private void internalBuild(File file, ModelFactory<T> modelFactory) {
        modelFactory.preBuilt();

        if (!preBuilt) {
            preBuild(modelFactory);
        }

        this.validateWorkbook(file);

        writableConfiguration.setFile(file);

        try (
                InputStream is = Files.newInputStream(file.toPath());
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(modelConfiguration.getRowCacheSize())
                        .bufferSize(modelConfiguration.getBufferSize())
                        .open(is)
        ) {
            this.process(workbook, modelFactory);
        } catch (Exception e) {
            throw new ModelException(e);
        }
    }

    private void process(Workbook workbook, ModelFactory<T> modelFactory) {
        Sheet sheet = this.findSheet(workbook);

        writableConfiguration.setSheetName(sheet.getSheetName());

        int rowCount = 0;
        for (Row row : sheet) {
            if (rowCount == 0) {

                if (!columnsBuilded && !isRowEmpty(row)) {

                    this.iterateCells(row, (cell, position) -> {
                        String headerName = cell.getStringCellValue();
                        if (!StringUtils.isEmpty(headerName)) {
                            AttributeConfiguration attributeConfiguration = this.findByHeaderName(headerName);
                            if (Objects.nonNull(attributeConfiguration)) {
                                attributeConfiguration.setCol(position);
                                modelConfiguration.getMappings().put(attributeConfiguration.getFieldName(), attributeConfiguration);
                            }
                        }
                    });

                    columnsBuilded = true;
                }
            } else if (!isRowEmpty(row)) {

                ItemFactory item = modelFactory.item();
                ItemErrorExtension itemErrorExtension = ModelUtils.convertTo(item);
                itemErrorExtension.withRow(rowCount);
                itemErrorExtension.withErrorListener(consumerListener);

                this.iterateCells(row, (cell, position) -> {
                    AttributeConfiguration attributeConfiguration = this.findByPosition(position);
                    if (Objects.nonNull(attributeConfiguration)) {
                        this.bindCellTypeValue(item, cell, attributeConfiguration, itemErrorExtension, modelFactory);
                    }
                });

            }

            rowCount++;
        }

    }

    private void validateWorkbook(File file) {
        Assert.isTrue(file.exists(), "File " + file.getName() + " does not exist");
    }

    private Sheet findSheet(Workbook workbook) {
        Integer sheetIndex = modelConfiguration.getSheetIndex();
        String sheetName = modelConfiguration.getSheetName();

        if (Objects.nonNull(sheetIndex)) {
            if (workbook.getNumberOfSheets() <= sheetIndex) {
                throw new MissingSheetException("Sheet index " + sheetIndex + " is out of bounds");
            }
            return workbook.getSheetAt(sheetIndex);
        }

        if (StringUtils.isNotEmpty(sheetName)) {
            return workbook.getSheet(sheetName);
        }

        return workbook.getSheetAt(0);
    }

    private AttributeConfiguration findByAttribute(String fieldName) {
        return Optional.ofNullable(modelConfiguration.getMappings().get(fieldName))
                .orElse(new AttributeConfiguration(fieldName));
    }

    private AttributeConfiguration findByHeaderName(String headerName) {
        return modelConfiguration.getMappings().values()
                .stream()
                .filter(a -> StringUtils.isNotEmpty(a.getHeaderName()) && a.getHeaderName().equals(headerName))
                .findFirst()
                .orElse(null);
    }

    private AttributeConfiguration findByPosition(int position) {
        return modelConfiguration.getMappings().values()
                .stream()
                .filter(a -> Objects.nonNull(a.getCol()) && a.getCol() == position)
                .findFirst()
                .orElse(null);
    }

    private boolean isRowEmpty(Row row) {
        try {
            for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
                Cell cell = row.getCell(c);
                if (cell != null && !cell.getCellType().equals(CellType.BLANK))
                    return false;
            }
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    private void iterateCells(Row row, BiConsumer<Cell, Integer> consumer) {
        int col = 0;
        for (Cell cell : row) {
            consumer.accept(cell, col);
            col++;
        }
    }

    private void bindFieldType(Class<?> clazz, String fieldName, AttributeConfiguration attributeConfiguration, boolean isHierarchical) {
        try {
            if (isHierarchical) {
                String[] nameParts = fieldName.split("\\.");
                if (nameParts.length == 1) {
                    attributeConfiguration.setFieldType(clazz);
                    return;
                }

                this.iterateParts(nameParts, 0, clazz, attributeConfiguration);
            } else {
                attributeConfiguration.setFieldType(clazz.getDeclaredField(fieldName).getType());
            }
        } catch (Exception e) {
            throw new ModelException(e);
        }

    }

    private void iterateParts(String[] nameParts, int pos, Class<?> clazz, AttributeConfiguration attributeConfiguration) throws Exception {
        String part = nameParts[pos];
        boolean isLast = pos == nameParts.length - 1;
        Class<?> childClazz = clazz.getDeclaredField(part).getType();
        if (isLast) {
            attributeConfiguration.setFieldType(childClazz);
        } else {
            iterateParts(nameParts, ++pos, childClazz, attributeConfiguration);
        }
    }

    private void bindCellTypeValue(ItemFactory item, Cell cell, AttributeConfiguration attributeConfiguration, ItemErrorExtension itemErrorExtension, ModelFactory<T> modelFactory) {
        try {
            Object value = null;

            Class<?> clastClass = null;
            switch (cell.getCellType()) {
                case STRING:
                case BLANK:
                case FORMULA:
                case ERROR:
                    value = cell.getStringCellValue();
                    break;
                case BOOLEAN:
                    value = cell.getBooleanCellValue();
                    break;
                case NUMERIC:
                    clastClass = attributeConfiguration.getFieldType();
                    if (DateUtil.isCellDateFormatted(cell) && Objects.nonNull(clastClass)) {

                        if (clastClass.isAssignableFrom(Date.class)) {
                            value = cell.getDateCellValue();
                        } else if (clastClass.isAssignableFrom(LocalDate.class)) {
                            value = cell.getLocalDateTimeCellValue().toLocalDate();
                        } else if (clastClass.isAssignableFrom(LocalTime.class)) {
                            value = cell.getLocalDateTimeCellValue().toLocalTime();
                        } else if (clastClass.isAssignableFrom(LocalDateTime.class)) {
                            value = cell.getLocalDateTimeCellValue();
                        } else {
                            value = cell.getNumericCellValue();
                            clastClass = null;
                        }
                    } else {
                        value = cell.getNumericCellValue();
                        clastClass = null;
                    }

                    break;
                default:
                    throw new ModelException("Unknown cell type " + cell.getCellType());
            }

            value = modelFactory.getModelMapper().mapConverterValue(cell, value, attributeConfiguration);

            item.withValue(attributeConfiguration.getFieldName(), value, clastClass);
        } catch (Exception e) {
            itemErrorExtension.withError(e);
            if (!ModelUtils.convertTo(modelFactory, ModelValidatorExtension.class).isSaveErrors()) {
                throw new ModelException(e.getMessage(), e);
            }
        }
    }

    private void cleanHierarchical() {
        Map<String, AttributeConfiguration> mappings = modelConfiguration.getMappings().values().stream()
                .filter(m -> !ModelUtils.isHierarchical(m.getFieldName()) && !m.isIgnored())
                .collect(Collectors.toMap(a -> a.getFieldName(), a -> a));

        this.modelConfiguration.getMappings().clear();
        this.modelConfiguration.getMappings().putAll(mappings);
    }

    private List<T> buildFactory(ModelFactory<T> modelFactory) {
        List<T> results = modelFactory.build();

        if (this.writableConfiguration.isErrorFile()) {
            try (FileInputStream inputStream = new FileInputStream(this.writableConfiguration.getFile())) {
                this.writableConfiguration.setWorkbook(WorkbookFactory.create(inputStream));

                CellStyle errorStyle = null;
                int[] dataLen = {0};
                int errorColumn = 0;

                Sheet sheet = this.writableConfiguration.getWorkbook().getSheet(this.writableConfiguration.getSheetName());
                String value = "ERROR";
                this.addAutoSizeValue(dataLen, value);

                int pos = 0;
                for (Row row : sheet) {
                    if (pos == 0) {
                        Row headerRow = sheet.getRow(0);
                        errorColumn = headerRow.getLastCellNum();

                        Cell errorCell = headerRow.createCell(errorColumn, CellType.STRING);
                        errorCell.setCellStyle(headerRow.getCell(errorColumn - 1).getCellStyle());
                        errorCell.setCellValue(value);
                    } else {
                        ItemErrorExtension<T> error = this.findErrorbyRow(pos);
                        if (Objects.isNull(error)) {
                            sheet.removeRow(row);
                        } else {
                            Row dataRow = sheet.getRow(error.getRow());
                            Cell dataCell = dataRow.createCell(errorColumn, CellType.STRING);
                            value = error.getGroupedErrors();
                            this.addAutoSizeValue(dataLen, value);

                            dataCell.setCellValue(value);

                            if (Objects.isNull(errorStyle)) {
                                errorStyle = this.writableConfiguration.getWorkbook().createCellStyle();
                                Cell memberCell = dataRow.getCell(dataRow.getLastCellNum() - 2);
                                CellStyle memberStyle = memberCell.getCellStyle();
                                if (Objects.nonNull(memberStyle)) {
                                    errorStyle.cloneStyleFrom(memberStyle);
                                }
                                errorStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                                errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                            }
                            dataCell.setCellStyle(errorStyle);
                        }
                    }
                    pos++;
                }

                int calculated = ExcelUtils.calculateAutoSize(dataLen[0]);
                sheet.setColumnWidth(errorColumn, calculated);
            } catch (Exception e) {
                throw new ModelException(e);
            }
        }

        return results;
    }

    private void addAutoSizeValue(int[] len, String value) {
        if (Objects.nonNull(value)) {
            int l = value.length();
            if (l > len[0]) {
                len[0] = l;
            }
        }
    }

    private ItemErrorExtension<T> findErrorbyRow(int row) {
        return errorList.stream().filter(e -> e.getRow() == row).findFirst().orElse(null);
    }

    private ValidatedFactory<T, M> buildValidatedFactory(File excelFile) {
        ModelFactory<T> modelFactory = this.prepareModelFactory(excelFile, true, true);
        ModelValidatorExtension<T> modelValidatorExtension = ModelUtils.convertTo(modelFactory);
        List<T> all = this.buildFactory(modelFactory);
        return new ValidatedFactory<>(new Validated(modelValidatorExtension, all), modelFactory, modelValidatorExtension);
    }

    private ValidatedFactory<T, M> buildValidatedErrorFactory(File excelFile) {
        ModelFactory<T> modelFactory = this.prepareModelFactory(excelFile, false, true);
        ModelValidatorExtension<T> modelValidatorExtension = ModelUtils.convertTo(modelFactory);
        List<T> all = this.buildFactory(modelFactory);
        return new ValidatedFactory<>(new ValidatedError(modelValidatorExtension, all), modelFactory, modelValidatorExtension);
    }

    private ModelFactory<T> prepareModelFactory(File excelFile, boolean saveSuccessful, boolean saveErrors) {
        Assert.notNull(excelFile, "Excel file must not be null");
        ModelFactory<T> modelFactory = Factory.getModelFactory(modelClass, mapClass);
        ModelValidatorExtension<T> modelValidatorExtension = ModelUtils.convertTo(modelFactory);
        modelValidatorExtension.withSaveSuccessful(saveSuccessful);
        modelValidatorExtension.withSaveErrors(saveErrors);
        this.populateModelFactory(modelFactory);
        this.internalBuild(excelFile, modelFactory);
        return modelFactory;
    }

    private void populateModelFactory(ModelFactory<T> modelFactory) {
        if (Objects.nonNull(mapper)) {
            modelFactory.withModelMapper(mapper);
        }
        if (Objects.nonNull(mapModel)) {
            ModelUtils.convertTo(modelFactory, ModelMapFactory.class).withMapModel(mapModel);
        }
        modelFactory.toValidator().withValidator(validator).withValidator(jakartaValidator);
    }

    @Data
    private static class WritableConfiguration {
        private boolean errorFile;
        private File file;
        private String sheetName;
        private Workbook workbook;
        private Path errorFolder;
        private String fileName;
        private boolean versioned;

        public File executeFile(boolean hasErrors) {
            if (!hasErrors) return null;
            return execute((w, f) -> {
                try (FileOutputStream outputStream = new FileOutputStream(f)) {
                    w.write(outputStream);
                } catch (IOException e) {
                    throw new ExcelException(e);
                } finally {
                    dispose();
                }
                return f;
            });
        }

        public View executeView(boolean hasErrors) {
            if (!hasErrors) return null;
            return execute((w, f) -> {
                return new ErrorView(w, file);
            });
        }

        private <T> T execute(BiFunction<Workbook, File, T> consumer) {
            if (errorFile && Objects.nonNull(workbook)) {
                if (Objects.isNull(errorFolder)) {
                    errorFolder = Path.of(System.getProperty("java.io.tmpdir"));
                }

                fileName = SupportUtils.makeFileNameWithPrefix("", StringUtils.isEmpty(fileName) ? SupportUtils.generateDefaultFileName("") : fileName, versioned, false);

                return consumer.apply(workbook, errorFolder.resolve(fileName).toFile());
            }
            return null;
        }

        public void dispose() {
            if (errorFile && Objects.nonNull(workbook)) {
                try {
                    workbook.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    @Getter
    private static class ValidatedFactory<T, M> {
        private final Validated<T> validated;
        private final ValidatedError<T> validatedError;
        private final ModelFactory<T> modelFactory;
        private final ModelValidatorExtension<T> modelValidatorExtension;

        public ValidatedFactory(Validated<T> validated, ModelFactory<T> modelFactory, ModelValidatorExtension<T> modelValidatorExtension) {
            this.validated = validated;
            this.validatedError = null;
            this.modelFactory = modelFactory;
            this.modelValidatorExtension = modelValidatorExtension;
        }

        public ValidatedFactory(ValidatedError<T> validatedError, ModelFactory<T> modelFactory, ModelValidatorExtension<T> modelValidatorExtension) {
            this.validated = null;
            this.validatedError = validatedError;
            this.modelFactory = modelFactory;
            this.modelValidatorExtension = modelValidatorExtension;
        }

        public ModelMapFactory<T, M> getModelMapFactory() {
            return ModelUtils.convertTo(modelFactory);
        }
    }
}
