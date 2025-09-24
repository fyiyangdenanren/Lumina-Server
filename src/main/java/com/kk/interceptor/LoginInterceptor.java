package com.kk.interceptor;

import cn.hutool.core.util.StrUtil;
import com.kk.constants.HttpStatus;
import com.kk.exception.CustomException;
import com.kk.utils.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.validation.constraints.NotNull;
public class LoginInterceptor implements HandlerInterceptor {
    public boolean preHandle(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response, @NotNull final Object handler) {
        // 1.拦截
        if (StrUtil.isBlank(UserContextHolder.getUserId())) {
            throw new CustomException("您暂未登录，请重新登录", HttpStatus.UNAUTHORIZED);
        }
        // 2.放行
        return true;
    }

}
