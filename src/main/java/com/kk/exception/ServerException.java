package com.kk.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private int code;
    private String message;

    public ServerException() {

    }

    public ServerException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }
}