package com.cmeza.spring.excel.repository.dsl.models;

import com.cmeza.spring.excel.repository.support.transform.Transform;
import lombok.Data;

import java.util.Map;

@Data
public class MappingDsl implements Transform<Map<String, Object>> {
    private String fieldName;
    private String headerName = "";
    private String styleAliasBean = "";

    @Override
    public Map<String, Object> transform() {
        return Map.of("fieldName", fieldName, "headerName", headerName, "styleAliasBean", styleAliasBean);
    }
}
