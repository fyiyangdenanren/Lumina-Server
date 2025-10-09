package com.kk.controller;

import com.kk.domain.po.R;
import com.kk.repository.IMySQLChatHistoryRepository;
import com.kk.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final IMySQLChatHistoryRepository mySQLChatHistoryRepository;
    private final IChatService chatService;
    // private final IMongoChatHistoryRepository mongoChatHistoryRepository;

    /**
     * 启动一个流式聊天（框架会对返回的 Flux 进行订阅并将内容推给客户端）
     */
    @GetMapping(value = "/stream", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(@RequestParam(value = "prompt") String prompt, @RequestParam(value = "conversationId") Long conversationId) {
        // 1.保存会话conversation
        mySQLChatHistoryRepository.save(prompt, conversationId);
        // 2.大模型交互
        return chatService.chatStream(conversationId, prompt);
    }

    /**
     * 手动取消正在进行的聊天流
     */
    @PostMapping("/abort/{conversationId}")
    public R<String> abortChat(@PathVariable Long conversationId) {
        String msg = chatService.abortChat(conversationId);
        return R.ok(msg);
    }
}
