package com.kk.domain.dto;

import lombok.Data;

@Data
public class LoginDTO {
    /**
     * 姓名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 验证码
     */
    private String code;
}
