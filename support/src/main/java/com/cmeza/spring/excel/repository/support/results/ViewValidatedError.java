package com.cmeza.spring.excel.repository.support.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.servlet.View;

@Getter
@Setter
@ToString(callSuper = true)
public class ViewValidatedError<T> extends ValidatedError<T> {

    private View viewError;

    public ViewValidatedError(ValidatedError<T> validatedError, View viewError) {
        super(validatedError);
        this.viewError = viewError;
    }
}
