package com.cmeza.spring.excel.repository.models;

import com.cmeza.spring.excel.repository.annotations.TestFieldAnnotation;
import com.cmeza.spring.excel.repository.annotations.TestStyleAnnotation;
import lombok.Data;

import static com.cmeza.spring.excel.repository.tests.units.ExcelConverterTest.HEADER_STYLE;

@Data
public class Person {
    @TestStyleAnnotation(styleAlias = HEADER_STYLE)
    @TestFieldAnnotation(header = "PERSON ID")
    private Long id;

    @TestStyleAnnotation(styleAlias = HEADER_STYLE)
    @TestFieldAnnotation(header = "PERSON NAME")
    private String name;

    @TestStyleAnnotation(bold = true, italic = true, color = 25)
    @TestFieldAnnotation(header = "PERSON LASTNAME")
    private String lastName;
}
