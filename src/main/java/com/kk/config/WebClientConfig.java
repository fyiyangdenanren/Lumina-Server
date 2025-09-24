package com.kk.config;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;

@Configuration
public class WebClientConfig {
    // 简单方案：对去往 dashscope.aliyuncs.com 的请求添加固定 bearer token（仅示例）
    @Bean
    public WebClientCustomizer mcpWebClientCustomizer() {
        return builder -> builder.filter((request, next) -> {
                    String url = request.url().toString();
                    if (url.contains("dashscope.aliyuncs.com")) {
                        ClientRequest newReq = ClientRequest.from(request)
                                .headers(h -> h.setBearerAuth("sk-4f439668d4a04b09b83ac8d93b62f59f"))
                                .build();
                        return next.exchange(newReq);
                    }
                    return next.exchange(request);
                }).codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(16 * 1024 * 1024)) ;
    }

}

