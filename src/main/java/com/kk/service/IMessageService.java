package com.kk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kk.domain.po.Msg;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kk
 * @since 2025-08-02
 */
public interface IMessageService extends IService<Msg> {
    /**
     * 根据会话ID获取最后一条消息的ID
     * @param conversationId 会话ID
     * @return 最后一条消息的ID，如果不存在则返回null
     */
    Long getLastMessageIdByConversationId(String conversationId);
}