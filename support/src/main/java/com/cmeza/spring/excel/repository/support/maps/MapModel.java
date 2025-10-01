package com.cmeza.spring.excel.repository.support.maps;

public interface MapModel<T, E> {
    E map(int position, T entity);
}
