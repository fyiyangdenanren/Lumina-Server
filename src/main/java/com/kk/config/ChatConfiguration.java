package com.kk.config;

import com.kk.advisor.RAGAdvisor;
import com.kk.memory.MySQLChatMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import reactor.core.scheduler.Schedulers;

import static com.kk.constants.PromptConstant.SYSTEM_PROMPT;

@Configuration
@RequiredArgsConstructor
public class ChatConfiguration {
    private final RAGAdvisor ragAdvisor;
    private final ToolCallbackProvider toolCallbackProvider;

    /**
     * 配置基于 OpenAI 的对话客户端
     */
    @Bean
    public ChatClient chatClient(MySQLChatMemory chatMemory, OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(
                        // SimpleLoggerAdvisor.builder().build(),
                        MessageChatMemoryAdvisor.builder(chatMemory).order(Ordered.HIGHEST_PRECEDENCE)
                                .conversationId(ChatMemory.CONVERSATION_ID)
                                .scheduler((Schedulers.boundedElastic())).build(),
                        ragAdvisor
                )
                .build();
    }

}
