package com.kk.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private int code;

    public ServerException() {

    }

    public ServerException(String message, int code) {
        super(message);
        this.code = code;
    }
}