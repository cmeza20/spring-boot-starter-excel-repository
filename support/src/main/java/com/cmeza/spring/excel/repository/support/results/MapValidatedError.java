package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class MapValidatedError<T, E> extends ValidatedError<T> {

    private List<E> mapList;

    public MapValidatedError(ValidatedError<T> validatedError, List<E> maps) {
        super(validatedError);
        this.mapList = maps;
    }
}
