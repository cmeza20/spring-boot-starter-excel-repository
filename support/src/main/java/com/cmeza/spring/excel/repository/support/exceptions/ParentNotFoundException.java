package com.cmeza.spring.excel.repository.support.exceptions;

public class ParentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ParentNotFoundException(String message) {
        super(message);
    }

    public ParentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParentNotFoundException(Throwable cause) {
        super(cause);
    }
}
