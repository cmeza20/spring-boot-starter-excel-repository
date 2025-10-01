package com.cmeza.spring.excel.repository.models;

import com.cmeza.spring.excel.repository.support.annotations.model.Sheet;
import com.cmeza.spring.excel.repository.support.annotations.model.Style;
import lombok.Data;

@Data
@Sheet(name = "Titles")
@Style(name = "customHeaderStyleConfiguration")
public class Title {
    private Long id;
    private String name;

    public Title setId(Long id) {
        this.id = id;
        return this;
    }

    public Title setName(String name) {
        this.name = name;
        return this;
    }
}
