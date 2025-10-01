package com.cmeza.spring.excel.repository.builders;

import com.cmeza.spring.ioc.handler.metadata.ClassMetadata;
import com.cmeza.spring.ioc.handler.metadata.MethodMetadata;

public interface ExtraBuilder {
    void applyAfterMethodProcessor(ClassMetadata classMetadata, MethodMetadata methodMetadata);

    void applyOnEndMethod();
}
