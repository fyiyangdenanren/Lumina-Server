package com.kk.service.impl;

import com.kk.constants.HttpStatus;
import com.kk.exception.ServerException;
import com.kk.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscription;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatClient chatClient;
    public static final ConcurrentMap<Long, Subscription> subscriptions = new ConcurrentHashMap<>();

    @Override
    public Flux<String> chatStream(final Long conversationId, final String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(advisors -> {
                    advisors.param(ChatMemory.CONVERSATION_ID, conversationId);
                })
                .stream()
                .content()
                // 当框架/客户端订阅时保存 Subscription（便于 later cancel）
                .doOnSubscribe(subscription -> {
                    // 保存 subscription（若已有旧的，先取消旧的）
                    Subscription old = subscriptions.put(conversationId, subscription);
                    if (old != null) {
                        try {
                            old.cancel();
                        } catch (Exception ignored) {
                        }
                    }
                })
                // 流完成 / 出错 / 取消 时清理 map 防止内存泄漏
                .doFinally(signal -> subscriptions.remove(conversationId));
    }

    @Override
    public String abortChat(final Long conversationId) {
        Subscription sub = subscriptions.remove(conversationId);
        if (sub != null) {
            try {
                sub.cancel();
            } catch (ServerException e) {
                throw new ServerException(e.getMessage(), HttpStatus.ERROR);
            }
            return "已中断会话";
        } else {
            return "会话不存在";
        }
    }

   /* @Override
    public String generateChart(final Long conversationId,final String userPrompt) {
        // 1. 调用 AI 生成配置
        String aiOutput = noRag.prompt()
                .system(PYTHON_CODE_GEN_PROMPT)
                .user(userPrompt)
                .advisors(advisors -> advisors.param(MySQLChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        // 2. 容错解析
        ChartSpec spec = jsonUtil.parseSafe(aiOutput, ChartSpec.class);

        // 3. 调用 Python 渲染
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8000/render";
        Map map = restTemplate.postForObject(url, spec, Map.class);
        return (String) map.get("html");
    }*/

}
