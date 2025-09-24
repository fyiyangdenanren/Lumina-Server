package com.kk.repository;

import java.util.List;

public interface IMongoChatHistoryRepository {
    List<String> findAllConversationIds(String userId);

    void save(String prompt, Long conversationId);

    void remove(String conversationId);
}
