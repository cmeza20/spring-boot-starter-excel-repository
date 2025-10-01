package com.cmeza.spring.excel.repository.support.exceptions;

public class HeaderNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HeaderNotFoundException(String message) {
        super(message);
    }

    public HeaderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeaderNotFoundException(Throwable cause) {
        super(cause);
    }
}
