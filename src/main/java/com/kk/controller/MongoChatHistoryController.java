package com.kk.controller;

import com.kk.domain.po.R;
import com.kk.memory.MongoChatMemory;
import com.kk.repository.impl.MongoChatHistoryRepository;
import com.kk.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mongo")
@RequiredArgsConstructor
public class MongoChatHistoryController {
    private final MongoChatMemory chatMemory;
    private final MongoChatHistoryRepository chatHistory;

    /**
     * 获取会话详细信息
     */
    @GetMapping("/history/{conversationId}")
    public R<List<Message>> getChatHistory(@PathVariable String conversationId) {
        List<Message> history = chatMemory.get(conversationId);
        return R.ok(history);
    }

    /**
     * 获取会话历史列表
     */
    @GetMapping("/getConversationIds")
    public R<List<String>> getConversationIds() {
        String userId = UserContextHolder.getUserId();
        List<String> conversationIds = chatHistory.findAllConversationIds(userId);
        return R.ok(conversationIds);
    }

    /**
     * 删除指定 conversationId 的对话记录
     */
    @DeleteMapping("/conversations/{conversationId}")
    public R<Boolean> deleteConversation(@PathVariable String conversationId) {
        chatMemory.clear(conversationId);
        return R.ok(true);
    }

}
