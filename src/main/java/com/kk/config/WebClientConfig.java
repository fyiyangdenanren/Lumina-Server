package com.kk.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {
    @Bean
    public WebClientCustomizer mcpWebClientCustomizer(Environment env) {
        // 建议把 token 放到配置文件（application.yml）中：mcp.dashscope.token=sk-...
        String dashToken = env.getProperty("spring.ai.openai.api-key", "");

        // 记录请求（header + uri）
        ExchangeFilterFunction logRequest = ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.debug("WebClient Request: {} {}", req.method(), req.url());
            req.headers().forEach((k, v) -> log.debug("Request header: {}={}", k, v));
            return Mono.just(req);
        });

        // 记录响应并在错误时打印 body
        ExchangeFilterFunction logResponse = ExchangeFilterFunction.ofResponseProcessor(resp -> {
            log.debug("WebClient Response status: {}", resp.statusCode());
            // 如果需要更多细节（body 等），在 retrieve() 时使用 onStatus 处理
            return Mono.just(resp);
        });

        // 针对 dashscope 的 token 自动注入与通用日志/错误 filter
        return builder -> builder
                .filter((request, next) -> {
                    String url = request.url().toString();
                    if (url.contains("dashscope.aliyuncs.com")) {
                        ClientRequest newReq = ClientRequest.from(request)
                                // 不要在代码中硬编码 token，示例只是展示如何注入
                                .headers(h -> {
                                    if (!dashToken.isBlank()) {
                                        h.setBearerAuth(dashToken);
                                    }
                                    // 明确表明我们希望 JSON 响应
                                    h.set("Accept", "application/json");
                                })
                                .build();
                        return next.exchange(newReq);
                    }
                    return next.exchange(request);
                })
                .filter(logRequest)
                .filter(logResponse)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024));
    }
}

