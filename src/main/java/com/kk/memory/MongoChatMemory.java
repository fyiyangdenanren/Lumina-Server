package com.kk.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kk.domain.po.ChatMessages;
import com.kk.utils.MessageSerializer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MongoChatMemory implements ChatMemory {
    private final MongoTemplate mongoTemplate;

    @Override
    public void add(@NonNull String conversationId, @NonNull List<Message> messages) {
        Query query = new Query(Criteria.where("conversationId").is(conversationId));
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);

        List<Message> updatedMessages;
        if (chatMessages != null) {
            try {
                updatedMessages = new java.util.ArrayList<>(chatMessages.getMessagesJson() != null
                        ? MessageSerializer.messagesFromJson(chatMessages.getMessagesJson()) : Collections.emptyList());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化消息失败", e);
            }
            updatedMessages.addAll(messages);
        } else {
            updatedMessages = new java.util.ArrayList<>(messages);
        }

        try {
            String json = MessageSerializer.messagesToJson(updatedMessages);
            if (chatMessages != null) {
                Update update = new Update().set("messagesJson", json);
                mongoTemplate.updateFirst(query, update, ChatMessages.class);
            } else {
                ChatMessages newChatMessages = new ChatMessages();
                newChatMessages.setConversationId(conversationId);
                newChatMessages.setMessagesJson(json);
                mongoTemplate.insert(newChatMessages);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化消息失败", e);
        }
    }

    @Override
    @NonNull
    public List<Message> get(@NonNull String conversationId) {
        Query query = new Query(Criteria.where("conversationId").is(conversationId));
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);

        if (chatMessages == null || chatMessages.getMessagesJson() == null) {
            return Collections.emptyList();
        }

        try {
            return MessageSerializer.messagesFromJson(chatMessages.getMessagesJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化消息失败", e);
        }
    }

    @Override
    public void clear(@NonNull String conversationId) {
        Query query = new Query(Criteria.where("conversationId").is(conversationId));
        mongoTemplate.remove(query, ChatMessages.class);
    }
}
