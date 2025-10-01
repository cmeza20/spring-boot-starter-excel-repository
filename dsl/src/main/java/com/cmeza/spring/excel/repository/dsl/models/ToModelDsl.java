package com.cmeza.spring.excel.repository.dsl.models;

import com.cmeza.spring.excel.repository.support.mappers.ToModelMapper;
import com.cmeza.spring.excel.repository.support.transform.Transform;
import com.cmeza.spring.excel.repository.support.constants.SupportConstants;
import com.cmeza.spring.excel.repository.support.maps.MapModel;
import lombok.Data;

import java.util.*;

@Data
public class ToModelDsl implements Transform<Map<String, Object>> {
    private boolean loggable;
    private String modelConfigurationBean = "";
    private boolean hierarchical = true;
    private int sheetIndex;
    private String sheetName = "";
    private int rowCacheSize = SupportConstants.DEFAULT_ROW_CACHE_SIZE;
    private int bufferSize = SupportConstants.DEFAULT_BUFFER_SIZE;
    private Class<? extends ToModelMapper<?>> mapper;
    private Class<? extends MapModel<?, ?>> map;
    private List<MappingDsl> mapping;
    private ErrorDsl error = new ErrorDsl();

    @Override
    public Map<String, Object> transform() {
        List<Map<String, Object>> mappingResult = new ArrayList<>();

        if (Objects.nonNull(mapping)) {
            mapping.forEach(mappingDsl ->
                    mappingResult.add(mappingDsl.transform()));
        }

        Map<String, Object> mapResult = new HashMap<>();
        mapResult.put("loggable", loggable);
        mapResult.put("modelConfigurationBean", modelConfigurationBean);
        mapResult.put("hierarchical", hierarchical);
        mapResult.put("sheetIndex", sheetIndex);
        mapResult.put("sheetName", sheetName);
        mapResult.put("rowCacheSize", rowCacheSize);
        mapResult.put("bufferSize", bufferSize);
        mapResult.put("mapper", mapper);
        mapResult.put("map", map);
        mapResult.put("mapping", mappingResult);
        mapResult.put("error", error.transform());
        return mapResult;
    }
}
