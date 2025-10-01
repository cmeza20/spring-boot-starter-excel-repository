package com.cmeza.spring.excel.repository.support.configurations.model;

import com.cmeza.spring.excel.repository.support.configurations.AbstractConfiguration;
import com.cmeza.spring.excel.repository.support.constants.SupportConstants;
import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import com.cmeza.spring.excel.repository.support.parsers.Parser;
import com.cmeza.spring.excel.repository.support.parsers.model.IModelParser;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Data
public class ModelConfiguration<T> extends AbstractConfiguration<ModelConfiguration<T>> {

    private Class<? extends ToModelMapper<T>> mapper;
    private Class<? extends MapModel<T, ?>> map;
    private boolean hierarchical = true;
    private Integer sheetIndex;
    private String sheetName;
    private int rowCacheSize = SupportConstants.DEFAULT_ROW_CACHE_SIZE;
    private int bufferSize = SupportConstants.DEFAULT_BUFFER_SIZE;
    private Map<String, AttributeConfiguration> mappings = new HashMap<>();
    private ErrorConfiguration error = new ErrorConfiguration();

    @Override
    public ModelConfiguration<T> cloneInstance() {
        ModelConfiguration<T> clone = new ModelConfiguration<>();
        Parser.getInstance().getParser(IModelParser.class).merge(this, clone);
        return clone;
    }

    public ModelConfiguration<T> addMapping(String fieldName) {
        AttributeConfiguration attributeConfiguration = new AttributeConfiguration(fieldName);
        return this.addMapping(attributeConfiguration);
    }

    public ModelConfiguration<T> addMapping(String fieldName, String headerName) {
        AttributeConfiguration attributeConfiguration = new AttributeConfiguration(fieldName)
                .setHeaderName(headerName);
        return addMapping(attributeConfiguration);
    }

    public ModelConfiguration<T> addMapping(String fieldName, String headerName, Class<?> fieldType) {
        AttributeConfiguration attributeConfiguration = new AttributeConfiguration(fieldName)
                .setHeaderName(headerName)
                .setFieldType(fieldType);
        return addMapping(attributeConfiguration);
    }

    public ModelConfiguration<T> addMapping(AttributeConfiguration attributeConfiguration) {
        Assert.notNull(attributeConfiguration, "AttributeConfiguration is required");
        Assert.hasLength(attributeConfiguration.getFieldName(), "Field name is required");
        this.mappings.putIfAbsent(attributeConfiguration.getFieldName(), attributeConfiguration);
        return this;
    }
}
