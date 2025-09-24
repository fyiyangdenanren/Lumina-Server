package com.kk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.domain.po.Conversation;
import com.kk.mapper.ConversationMapper;
import com.kk.service.IConversationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kk
 * @since 2025-08-02
 */
@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements IConversationService {

}
