package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.servlet.View;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class MapViewValidatedError<T, E> extends ViewValidatedError<T> {

    private List<E> mapList;

    public MapViewValidatedError(ValidatedError<T> validatedError, List<E> maps, View viewError) {
        super(validatedError, viewError);
        this.mapList = maps;
    }
}
