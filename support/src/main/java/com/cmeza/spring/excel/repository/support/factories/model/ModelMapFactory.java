package com.cmeza.spring.excel.repository.support.factories.model;

import com.cmeza.spring.excel.repository.support.maps.MapModel;

import java.util.List;

public interface ModelMapFactory<T, U> extends ModelFactory<T> {

    ModelMapFactory<T, U> withMapModel(MapModel<T, U> mapModel);

    List<U> buildMap();

}
