package com.cmeza.spring.excel.repository.support.exceptions;

public class ExecuteUnsupportedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExecuteUnsupportedException(String message) {
        super(message);
    }

    public ExecuteUnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteUnsupportedException(Throwable cause) {
        super(cause);
    }
}
