package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.servlet.View;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class MapViewValidated<T, E> extends ViewValidated<T> {

    private List<E> mapList;

    public MapViewValidated(Validated<T> validated, List<E> maps, View viewError) {
        super(validated, viewError);
        this.mapList = maps;
    }
}
