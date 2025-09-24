package com.kk.repository.impl;

import com.kk.domain.po.ChatMessages;
import com.kk.repository.IMongoChatHistoryRepository;
import com.kk.utils.UserContextHolder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class MongoChatHistoryRepository implements IMongoChatHistoryRepository {
    private final MongoTemplate mongoTemplate;

    /**
     * TODO保存会话至mongodb
     */
    @Override
    public void save(@NonNull final String prompt, @NonNull final Long conversationId) {
        // 1.判断该用户是否包含此次会话
        String userId = UserContextHolder.getUserId();
        // 2.查询mongodb中该用户下的所有会话ID，判断是否包含
        List<String> conversationIds = findAllConversationIds(userId);
        if (conversationIds == null) {
            conversationIds = Collections.emptyList();
        }
        // 2.1.包含该会话ID
        if (conversationIds.contains(conversationId.toString())) {
            return;
        }
        // 2.2.不包含此次会话
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setConversationId(conversationId.toString());
        chatMessages.setMessagesJson(prompt);

        // 4.保存

    }

    /**
     * TODO删除指定会话ID的会话记录
     *
     * @param conversationId 会话ID
     */
    @Override
    public void remove(final String conversationId) {

    }

    /**
     * 查询指定用户下的所有会话ID
     *
     * @param userId 用户ID
     * @return 所有会话ID列表
     */
    @Override
    public List<String> findAllConversationIds(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Collections.emptyList(); // 或抛出异常
        }
        // 构建正则表达式：以 userId + "_" 开头
        Pattern pattern = Pattern.compile("^" + Pattern.quote(userId) + "_");
        // 使用 regex 替代 matches
        Query query = new Query(Criteria.where("conversationId").regex(pattern));
        return mongoTemplate.findDistinct(
                query,
                "conversationId",
                ChatMessages.class,
                String.class
        );
    }

}
