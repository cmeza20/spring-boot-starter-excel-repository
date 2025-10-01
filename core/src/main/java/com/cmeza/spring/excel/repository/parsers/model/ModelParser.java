package com.cmeza.spring.excel.repository.parsers.model;

import com.cmeza.spring.excel.repository.dsl.models.MappingDsl;
import com.cmeza.spring.excel.repository.dsl.models.ToModelDsl;
import com.cmeza.spring.excel.repository.support.annotations.methods.ToModel;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.support.configurations.model.ModelConfiguration;
import com.cmeza.spring.excel.repository.support.constants.SupportConstants;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import com.cmeza.spring.excel.repository.parsers.excel.MappingParser;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.model.IModelParser;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelParser<T> implements IModelParser<ModelConfiguration<T>, ToModel, ToModelDsl> {

    private static final Parser parser = Parser.getInstance();

    @Override
    @SuppressWarnings("unchecked")
    public void parse(ToModel annotation, ModelConfiguration<T> modelConfiguration) {
        if (annotation.mapper().length > 0) {
            modelConfiguration.setMapper((Class<? extends ToModelMapper<T>>) annotation.mapper()[0]);
        }
        if (annotation.map().length > 0) {
            modelConfiguration.setMap((Class<? extends MapModel<T, ?>>) annotation.map()[0]);
        }
        if (annotation.sheetIndex() != 0) {
            modelConfiguration.setSheetIndex(annotation.sheetIndex());
        }
        if (StringUtils.isNotEmpty(annotation.sheetName())) {
            modelConfiguration.setSheetName(annotation.sheetName());
        }

        modelConfiguration.setHierarchical(annotation.hierarchical());
        modelConfiguration.setRowCacheSize(annotation.rowCacheSize());
        modelConfiguration.setBufferSize(annotation.bufferSize());

        MappingParser mappingParser = parser.getParser(MappingParser.class);

        for (Mapping mapping : annotation.mappings()) {
            mappingParser.parseModel(mapping, modelConfiguration);
        }

        parser.getParser(ErrorParser.class).parse(annotation.error(), modelConfiguration.getError());
    }

    @Override
    public void parseDsl(ToModel annotation, ToModelDsl dsl) {
        if (annotation.loggable()) {
            dsl.setLoggable(true);
        }
        if (annotation.mapper().length > 0) {
            dsl.setMapper(annotation.mapper()[0]);
        }
        if (annotation.map().length > 0) {
            dsl.setMap(annotation.map()[0]);
        }
        if (annotation.sheetIndex() != 0) {
            dsl.setSheetIndex(annotation.sheetIndex());
        }
        if (StringUtils.isNotEmpty(annotation.sheetName())) {
            dsl.setSheetName(annotation.sheetName());
        }
        if (annotation.hierarchical()) {
            dsl.setHierarchical(true);
        }
        if (annotation.rowCacheSize() != SupportConstants.DEFAULT_ROW_CACHE_SIZE) {
            dsl.setRowCacheSize(annotation.rowCacheSize());
        }
        if (annotation.bufferSize() != SupportConstants.DEFAULT_BUFFER_SIZE) {
            dsl.setBufferSize(annotation.bufferSize());
        }

        if (annotation.mappings().length > 0) {
            List<MappingDsl> mappings = new ArrayList<>();

            MappingParser mappingParser = parser.getParser(MappingParser.class);
            for (Mapping mapping : annotation.mappings()) {
                MappingDsl mappingDsl = new MappingDsl();
                mappingParser.parseDsl(mapping, mappingDsl);
                mappings.add(mappingDsl);

            }
            dsl.setMapping(mappings);
        }

        parser.getParser(ErrorParser.class).parseDsl(annotation.error(), dsl.getError());
    }

    @Override
    public void merge(ModelConfiguration<T> origin, ModelConfiguration<T> target) {
        if (Objects.nonNull(origin.getMapper())) {
            target.setMapper(origin.getMapper());
        }
        if (Objects.nonNull(origin.getMap())) {
            target.setMap(origin.getMap());
        }
        if (origin.isHierarchical()) {
            target.setHierarchical(true);
        }
        if (Objects.nonNull(origin.getSheetIndex())) {
            target.setSheetIndex(origin.getSheetIndex());
        }
        if (StringUtils.isNotEmpty(origin.getSheetName())) {
            target.setSheetName(origin.getSheetName());
        }
        if (origin.getRowCacheSize() != SupportConstants.DEFAULT_ROW_CACHE_SIZE) {
            target.setRowCacheSize(origin.getRowCacheSize());
        }
        if (origin.getBufferSize() != SupportConstants.DEFAULT_BUFFER_SIZE) {
            target.setBufferSize(origin.getBufferSize());
        }
        parser.getParser(MappingParser.class).mergeModel(origin, target);
        parser.getParser(ErrorParser.class).merge(origin.getError(), target.getError());
    }
}
