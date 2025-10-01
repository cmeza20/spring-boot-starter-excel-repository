package com.cmeza.spring.excel.repository.dsl.models;

import com.cmeza.spring.excel.repository.support.interceptors.ToExcelInterceptor;
import com.cmeza.spring.excel.repository.support.mappers.ToExcelMapper;
import com.cmeza.spring.excel.repository.support.transform.Transform;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ToExcelDsl implements Transform<Map<String, Object>> {
    private boolean loggable;
    private String excelConfigurationBean = "";
    private String path = "";
    private String fileName = "";
    private String prefix = "";
    private boolean versioned;
    private Class<? extends ToExcelInterceptor> interceptor;
    private Class<? extends ToExcelMapper> mapper;

    @Override
    public Map<String, Object> transform() {
        Map<String, Object> map = new HashMap<>();
        map.put("loggable", loggable);
        map.put("excelConfigurationBean", excelConfigurationBean);
        map.put("path", path);
        map.put("fileName", fileName);
        map.put("prefix", prefix);
        map.put("versioned", versioned);
        map.put("interceptor", interceptor);
        map.put("mapper", mapper);
        return map;
    }
}
