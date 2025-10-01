package com.cmeza.spring.excel.repository.repositories.definitions;

import com.cmeza.spring.ioc.handler.metadata.TypeMetadata;
import lombok.Data;

@Data
public class ParameterDefinition {
    private String parameterName;
    private int position;
    private boolean isBean;
    private boolean isBatch;
    private boolean isArray;
    private boolean isCollection;
    private boolean isFile;
    private boolean isPath;
    private TypeMetadata typeMetadata;
}
