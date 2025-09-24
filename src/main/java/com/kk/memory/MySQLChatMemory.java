package com.kk.memory;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kk.domain.po.Msg;
import com.kk.service.IConversationService;
import com.kk.service.IMessageService;
import com.kk.utils.TokenCounter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基于 MySQL 的会话记忆实现
 * 支持多轮对话的上下文存储和检索
 */
@Component
@RequiredArgsConstructor
public class MySQLChatMemory implements ChatMemory {
    private final IMessageService messageService;
    private final IConversationService conversationService;
    private final TokenCounter tokenCounter;

    /**
     * 添加会话上下文
     *
     * @param conversationId 会话ID
     * @param messages       消息列表
     */
    @Override
    public void add(@NonNull final String conversationId, @NonNull final List<Message> messages) {
        if (messages.isEmpty()) {
            return;
        }
        // 获取该会话中最后一条消息的ID，作为第一条新消息的parent_message_id
        Long parentId = messageService.getLastMessageIdByConversationId(conversationId);
        // 如果parentId为null（表示这是会话中的第一条消息），则设置为0
        if (parentId == null) {
            parentId = 0L;
        }
        // 逐条处理消息，维护消息链的parent关系
        for (Message message : messages) {
            Msg msg = new Msg();
            msg.setConversationId(Long.valueOf(conversationId));
            msg.setMessageType(message.getMessageType().getValue());
            msg.setContent(message.getText());
            msg.setMetadata(message.getMetadata());
            msg.setTokens(tokenCounter.countTokens(message.getText()));
            msg.setCreatedTime(LocalDateTime.now());
            // 设置parent_message_id指向前一条消息
            msg.setParentMessageId(parentId);
            // 保存单条消息以获取其生成的ID，作为下一条消息的parent
            messageService.save(msg);
            parentId = msg.getMessageId();
        }
    }

    /**
     * 获取会话上下文
     *
     * @param conversationId 会话ID
     * @return 会话上下文列表
     */
    @NonNull
    @Override
    public List<Message> get(@NonNull final String conversationId) {
        LambdaQueryWrapper<Msg> chatHistory = new LambdaQueryWrapper<Msg>().eq(Msg::getConversationId, Long.valueOf(conversationId))
                .orderByAsc(Msg::getCreatedTime)
                .last("LIMIT " + Integer.MAX_VALUE);
        List<Msg> msgs = messageService.list(chatHistory);
        if (CollUtil.isEmpty(msgs)) {
            return List.of();
        }
        return msgs.stream()
                .map(msg -> {
                    String type = msg.getMessageType();
                    String content = msg.getContent();
                    Message message;
                    return switch (type) {
                        case "system" -> message = new SystemMessage(content);
                        case "user" -> message = new UserMessage(content);
                        case "assistant" -> message = new AssistantMessage(content);
                        default -> throw new IllegalArgumentException("Unknown message type: " + type);
                    };
                })
                .toList();
    }

    /**
     * 清空会话上下文
     *
     * @param conversationId 会话ID
     */
    @Override
    @Transactional
    public void clear(@NonNull final String conversationId) {
        conversationService.removeById(conversationId);
        LambdaQueryWrapper<Msg> msgLambdaQueryWrapper = new LambdaQueryWrapper<Msg>()
                .eq(Msg::getConversationId, Long.valueOf(conversationId));
        messageService.remove(msgLambdaQueryWrapper);
    }

}