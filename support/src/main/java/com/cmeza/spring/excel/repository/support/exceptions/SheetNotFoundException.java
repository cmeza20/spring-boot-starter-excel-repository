package com.cmeza.spring.excel.repository.support.exceptions;

public class SheetNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SheetNotFoundException(String message) {
        super(message);
    }

    public SheetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SheetNotFoundException(Throwable cause) {
        super(cause);
    }
}
