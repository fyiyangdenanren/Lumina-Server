package com.kk.exception;

import lombok.*;

/**
 * 基础异常
 */
@Getter
public class BaseException extends RuntimeException {

    private int code;

    /**
     * 无参构造
     */
    public BaseException() {

    }

    /**
     * 有参构造
     */
    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }
}
