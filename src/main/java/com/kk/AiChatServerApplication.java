package com.kk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AiChatServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiChatServerApplication.class, args);
        log.info("LUMINA启动成功");
    }

}
