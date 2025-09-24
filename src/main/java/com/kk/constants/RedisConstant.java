package com.kk.constants;

public class RedisConstant {
    /**
     * 用户登录token
     */
    public static final String LOGIN_USER_TOKEN = "login:user:token:";

    /**
     * 用户注册验证码
     */
    public static final String REGISTER_USER_CODE = "register:user:code:";

    /**
     * 用户登录验证码
     */
    public static final String LOGIN_USER_CODE = "login:user:code:";

    /**
     * 用户登录验证码有效期
     */
    public static final Long LOGIN_CODE_TTL = 5L;

    /**
     * 用户注册验证码有效期
     */
    public static final Long REGISTER_CODE_TTL = 5L;

    /**
     * 邮件发送方
     */
    public static final String EMAIL_FROM = "2869244577@qq.com";
}
