package com.cmeza.spring.excel.repository.dsl.models;

import com.cmeza.spring.excel.repository.support.transform.Transform;
import lombok.Data;

import java.util.Map;

@Data
public class ErrorDsl implements Transform<Map<String, Object>> {
    private String fileName = "";
    private String folder = "";
    private boolean versioned;

    @Override
    public Map<String, Object> transform() {
        return Map.of("fileName", fileName, "folder", folder, "versioned", versioned);
    }
}
