package com.kk.handler;

import com.kk.domain.po.R;
import com.kk.exception.CustomException;
import com.kk.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     *
     * @param e 异常
     * @return 返回异常结果R
     */
    @ExceptionHandler(Exception.class)
    public R<String> error(Exception e) {
        log.error("未知异常信息：{}", e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 处理客户端异常
     *
     * @param ce 异常
     * @return 返回异常结果R
     */
    @ExceptionHandler(CustomException.class)
    public R<String> error(CustomException ce) {
        log.error("客户端异常信息：{}", ce.getMessage());
        return R.fail(ce.getCode(), ce.getMessage());
    }

    /**
     * 处理服务端异常
     *
     * @param se 异常
     * @return 返回异常结果R
     */
    @ExceptionHandler(ServerException.class)
    public R<String> error(ServerException se) {
        log.error("服务端异常信息：{}", se.getMessage());
        return R.fail(se.getCode(), "请联系工作人员处理");
    }

}