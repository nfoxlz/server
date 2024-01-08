package com.compete.mis.exceptions;

public abstract class MisException extends Exception {
    public MisException() {
    }

    public MisException(String message) {
        super(message);
    }
}
