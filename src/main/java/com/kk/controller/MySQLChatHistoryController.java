package com.kk.controller;

import cn.hutool.core.collection.CollUtil;
import com.kk.domain.po.R;
import com.kk.domain.vo.ConversationVO;
import com.kk.domain.vo.MessageVO;
import com.kk.memory.MySQLChatMemory;
import com.kk.repository.IMySQLChatHistoryRepository;
import com.kk.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class MySQLChatHistoryController {
    private final IMySQLChatHistoryRepository chatHistoryRepository;
    private final MySQLChatMemory chatMemory;

    /**
     * 获取会话历史列表
     *
     * @return
     */
    @GetMapping("/getHistories")
    public R<List<ConversationVO>> getHistory() {
        String userId = UserContextHolder.getUserId();
        List<ConversationVO> conversationVOS = chatHistoryRepository.getConversationIds(userId);
        return R.ok(conversationVOS);
    }

    /**
     * 获取会话详细信息
     *
     * @param conversationId
     * @return
     */
    @GetMapping("/getConversation/{conversationId}")
    public R<List<MessageVO>> getConversation(@PathVariable Long conversationId) {
        List<Message> messages = chatMemory.get(conversationId.toString());
        if (CollUtil.isEmpty(messages)) {
            return R.fail("会话不存在");
        }
        return R.ok(messages.stream().map(MessageVO::new).toList());
    }

    /**
     * 删除会话
     *
     * @param conversationId
     * @return
     */
    @DeleteMapping("/deleteConversation/{conversationId}")
    public R<Void> deleteConversation(@PathVariable Long conversationId) {
        chatMemory.clear(conversationId.toString());
        return R.ok();
    }


    /**
     * 修改会话
     *
     * @param conversationVO
     * @return
     */
    @PutMapping("/updateConversation")
    public R<Void> updateConversation(@RequestBody ConversationVO conversationVO) {
        chatHistoryRepository.updateConversation( conversationVO);
        return R.ok();
    }
}
