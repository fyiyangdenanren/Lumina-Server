package com.kk.interceptor;


import cn.hutool.core.util.StrUtil;
import com.kk.constants.HttpStatus;
import com.kk.exception.CustomException;
import com.kk.properties.JwtProperties;
import com.kk.utils.JwtUtil;
import com.kk.utils.UserContextHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.kk.constants.RedisConstant.LOGIN_USER_TOKEN;

public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    private final StringRedisTemplate stringRedisTemplate;

    public JwtInterceptor(final JwtProperties jwtProperties, final StringRedisTemplate stringRedisTemplate) {
        this.jwtProperties = jwtProperties;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(@NotNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler) {
        // 1.获取token
        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2.判断获取到的token是否有效
        String userId;
        Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
        try {
            userId = claims.get("userId", String.class);
        } catch (Exception e) {
            return true;
        }
        // 3.获取redis中的token
        String tokenKey = LOGIN_USER_TOKEN + userId;
        String redisToken = stringRedisTemplate.opsForValue().get(tokenKey);
        if (redisToken == null || !redisToken.equals(token)) {
            throw new CustomException("无效账号，请重试", HttpStatus.UNAUTHORIZED);
        }
        // 4.保存用户到上下文
        UserContextHolder.setUserId(userId);
        // 5.放行
        return true;
    }

    @Override
    public void afterCompletion(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler, final Exception ex) {
        // 3.清空用户上下文
        UserContextHolder.clear();
    }

}
