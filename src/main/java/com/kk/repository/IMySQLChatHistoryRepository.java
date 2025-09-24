package com.kk.repository;

import com.kk.domain.vo.ConversationVO;

import java.util.List;

public interface IMySQLChatHistoryRepository {

    void save(String prompt, Long conversationId);

    List<ConversationVO> getConversationIds(String userId);

    void updateConversation(ConversationVO conversationVO);
}
