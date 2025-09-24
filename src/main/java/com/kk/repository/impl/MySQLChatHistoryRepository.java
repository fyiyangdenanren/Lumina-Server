package com.kk.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kk.domain.po.Conversation;
import com.kk.domain.vo.ConversationVO;
import com.kk.repository.IMySQLChatHistoryRepository;
import com.kk.service.IConversationService;
import com.kk.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MySQLChatHistoryRepository implements IMySQLChatHistoryRepository {
    private final IConversationService conversationService;

    /**
     * 保存会话
     */
    @Override
    public void save(String prompt, Long conversationId) {
        // 1.判断该用户是否包含此次会话
        String userId = UserContextHolder.getUserId();
        LambdaQueryWrapper<Conversation> conversationQ = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .eq(Conversation::getIsDeleted, 0);
        List<Conversation> list = conversationService.list(conversationQ);
        List<Long> conversationIds;
        if (CollUtil.isNotEmpty(list)) {
            conversationIds = list.stream().map(Conversation::getConversationId).toList();
            // 2.包含此次会话
            if (conversationIds.contains(conversationId)) {
                return;
            }
        }
        // 3.不包含此次会话
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(Long.valueOf(userId));
        String title = prompt.length() > 20 ? prompt.substring(0, 20) : prompt;
        conversation.setTitle(title);
        // 4.保存
        conversationService.save(conversation);
    }

    /**
     * 查询用户历史记录列表
     */
    @Override
    public List<ConversationVO> getConversationIds(String userId) {
        LambdaQueryWrapper<Conversation> lambdaQueryWrapper = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .eq(Conversation::getIsDeleted, 0)
                .orderByDesc(Conversation::getCreatedTime);
        List<Conversation> list = conversationService.list(lambdaQueryWrapper);
        if (CollUtil.isEmpty(list)) {
            return List.of();
        }
        return BeanUtil.copyToList(list, ConversationVO.class);
    }

    /**
     * 修改会话标题
     */
    @Override
    public void updateConversation(final ConversationVO conversationVO) {
        // 1.判空
        if (StrUtil.isBlank(conversationVO.getTitle())) {
            conversationVO.setTitle("未命名");
        }
        // 2.修改会话
        LambdaUpdateWrapper<Conversation> conversationUW = new LambdaUpdateWrapper<Conversation>()
                .eq(Conversation::getConversationId, conversationVO.getConversationId())
                .set(Conversation::getTitle, conversationVO.getTitle());
        conversationService.update(conversationUW);
    }
}
