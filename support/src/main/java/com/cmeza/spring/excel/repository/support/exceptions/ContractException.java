package com.cmeza.spring.excel.repository.support.exceptions;

public class ContractException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ContractException(String message) {
        super(message);
    }

    public ContractException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContractException(Throwable cause) {
        super(cause);
    }
}
