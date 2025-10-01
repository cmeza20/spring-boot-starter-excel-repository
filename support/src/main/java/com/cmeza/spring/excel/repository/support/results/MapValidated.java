package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class MapValidated<T, E> extends Validated<T> {

    private List<E> mapList;

    public MapValidated(Validated<T> validated, List<E> maps) {
        super(validated);
        this.mapList = maps;
    }
}
