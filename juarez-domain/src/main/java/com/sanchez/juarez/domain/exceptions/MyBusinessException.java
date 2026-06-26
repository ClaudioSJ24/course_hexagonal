package com.sanchez.juarez.domain.exceptions;

public class MyBusinessException extends RuntimeException {
    public MyBusinessException(String message) {
        super(message);
    }
}
