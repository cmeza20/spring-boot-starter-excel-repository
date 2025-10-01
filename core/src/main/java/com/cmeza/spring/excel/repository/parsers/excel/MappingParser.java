package com.cmeza.spring.excel.repository.parsers.excel;

import com.cmeza.spring.excel.repository.dsl.models.MappingDsl;
import com.cmeza.spring.excel.repository.support.annotations.support.Mapping;
import com.cmeza.spring.excel.repository.support.configurations.excel.SheetConfiguration;
import com.cmeza.spring.excel.repository.support.configurations.model.ModelConfiguration;
import com.cmeza.spring.excel.repository.support.parsers.excel.IMappingParser;
import org.apache.commons.lang3.StringUtils;

public class MappingParser implements IMappingParser<SheetConfiguration, ModelConfiguration<?>, Mapping, MappingDsl> {

    @Override
    public void parse(Mapping mapping, SheetConfiguration sheetConfiguration) {
        if (StringUtils.isEmpty(mapping.headerName()) && StringUtils.isEmpty(mapping.styleAliasBean())) {
            sheetConfiguration.addMapping(mapping.value());
        } else if (StringUtils.isNotEmpty(mapping.headerName())) {
            sheetConfiguration.addMapping(mapping.value(), mapping.headerName());
        } else {
            sheetConfiguration.addMapping(mapping.value(), mapping.headerName(), mapping.styleAliasBean());
        }
    }

    @Override
    public void parseModel(Mapping mapping, ModelConfiguration<?> modelConfiguration) {
        if (StringUtils.isEmpty(mapping.headerName())) {
            modelConfiguration.addMapping(mapping.value());
        } else {
            modelConfiguration.addMapping(mapping.value(), mapping.headerName());
        }
    }

    @Override
    public void parseDsl(Mapping mapping, MappingDsl dsl) {
        if (StringUtils.isNotEmpty(mapping.value())) {
            dsl.setFieldName(mapping.value());
        }
        if (StringUtils.isNotEmpty(mapping.headerName())) {
            dsl.setHeaderName(mapping.headerName());
        }
    }

    @Override
    public  void mergeModel(ModelConfiguration<?> origin, ModelConfiguration<?> target) {
        if (!origin.getMappings().isEmpty()) {
            target.getMappings().putAll(origin.getMappings());
        }
    }

    @Override
    public void merge(SheetConfiguration origin, SheetConfiguration target) {
        if (!origin.getMappings().isEmpty()) {
            target.getMappings().addAll(origin.getMappings());
        }
    }
}
