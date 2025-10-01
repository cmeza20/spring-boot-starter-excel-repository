package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.servlet.View;

@Getter
@Setter
@ToString(callSuper = true)
public class ViewValidated<T> extends Validated<T> {

    private View viewError;

    public ViewValidated(Validated<T> validated, View viewError) {
        super(validated);
        this.viewError = viewError;
    }
}
