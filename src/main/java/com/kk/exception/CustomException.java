package com.kk.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private int code;
    private String message;

    public CustomException() {

    }

    public CustomException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }

}