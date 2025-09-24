package com.kk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.domain.po.Msg;
import com.kk.mapper.MessageMapper;
import com.kk.service.IMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kk
 * @since 2025-08-02
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Msg> implements IMessageService {

    @Override
    public Long getLastMessageIdByConversationId(String conversationId) {
        LambdaQueryWrapper<Msg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Msg::getConversationId, Long.valueOf(conversationId))
                .orderByDesc(Msg::getMessageId)
                .last("LIMIT 1");
        
        List<Msg> msgs = this.list(queryWrapper);
        return msgs.isEmpty() ? null : msgs.get(0).getMessageId();
    }
}