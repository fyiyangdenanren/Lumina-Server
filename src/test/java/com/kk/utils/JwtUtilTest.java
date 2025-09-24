package com.kk.utils;


import com.kk.properties.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void encode(){
        String password = BCryptUtil.hashPassword("1234");
        System.out.println(password);
    }

    @Test
    void checkCode(){
        boolean b = BCryptUtil.checkPassword("12345", "$2a$10$m2aJf9Ighy7Gyqcb3pMPRuCzKnOtcTyxsc5Bb0n96CSa9daNdvBUS");
        System.out.println(b);
    }

    @Test
    void createJWT() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", "");
        JwtUtil.createJWT(jwtProperties.getSecretKey(), jwtProperties.getTtlDays(), null);
    }

}
