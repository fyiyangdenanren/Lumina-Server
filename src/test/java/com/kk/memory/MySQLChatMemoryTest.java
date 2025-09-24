package com.kk.memory;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MySQLChatMemory 测试类
 * 演示会话记忆的基本功能：存储、检索和清理
 */
@SpringBootTest
@SpringJUnitConfig
public class MySQLChatMemoryTest {

    @Resource
    private MySQLChatMemory mySQLChatMemory;

    @Test
    public void testChatMemoryBasicOperations() {
        String conversationId = "test_conversation_123";
        
        // 清理可能存在的测试数据
        mySQLChatMemory.clear(conversationId);
        
        // 1. 测试添加消息
        List<Message> messages = List.of(
                new SystemMessage("你是一个有用的AI助手"),
                new UserMessage("你好，我叫张三"),
                new AssistantMessage("你好张三！很高兴认识你。有什么我可以帮助你的吗？")
        );
        
        mySQLChatMemory.add(conversationId, messages);
        
        // 2. 测试检索消息
        List<Message> retrievedMessages = mySQLChatMemory.get(conversationId);
        
        assertNotNull(retrievedMessages);
        assertEquals(3, retrievedMessages.size());
        
        // 验证消息内容和类型
        assertTrue(retrievedMessages.get(0) instanceof SystemMessage);
        assertTrue(retrievedMessages.get(1) instanceof UserMessage);
        assertTrue(retrievedMessages.get(2) instanceof AssistantMessage);
        
        assertTrue(retrievedMessages.get(0).toString().contains("你是一个有用的AI助手"));
        assertTrue(retrievedMessages.get(1).toString().contains("你好，我叫张三"));
        assertTrue(retrievedMessages.get(2).toString().contains("你好张三！很高兴认识你。有什么我可以帮助你的吗？"));
        
        // 3. 测试添加更多消息（模拟多轮对话）
        List<Message> additionalMessages = List.of(
                new UserMessage("我想了解一些历史知识"),
                new AssistantMessage("好的！我很乐意和你分享历史知识。你对哪个历史时期或者哪些历史事件比较感兴趣呢？")
        );
        
        mySQLChatMemory.add(conversationId, additionalMessages);
        
        // 4. 再次检索，应该包含所有5条消息
        List<Message> allMessages = mySQLChatMemory.get(conversationId);
        assertEquals(5, allMessages.size());
        
        // 5. 测试清理功能
        mySQLChatMemory.clear(conversationId);
        List<Message> emptyMessages = mySQLChatMemory.get(conversationId);
        assertTrue(emptyMessages.isEmpty());
        
        System.out.println("✅ MySQLChatMemory 测试通过！");
    }
    
    @Test
    public void testMultipleConversations() {
        String conv1 = "conversation_1";
        String conv2 = "conversation_2";
        
        // 清理测试数据
        mySQLChatMemory.clear(conv1);
        mySQLChatMemory.clear(conv2);
        
        // 为两个不同的会话添加消息
        mySQLChatMemory.add(conv1, List.of(
                new UserMessage("会话1的消息"),
                new AssistantMessage("这是会话1的回复")
        ));
        
        mySQLChatMemory.add(conv2, List.of(
                new UserMessage("会话2的消息"),
                new AssistantMessage("这是会话2的回复")
        ));
        
        // 验证会话隔离
        List<Message> conv1Messages = mySQLChatMemory.get(conv1);
        List<Message> conv2Messages = mySQLChatMemory.get(conv2);
        
        assertEquals(2, conv1Messages.size());
        assertEquals(2, conv2Messages.size());
        
        assertTrue(conv1Messages.get(0).toString().contains("会话1的消息"));
        assertTrue(conv2Messages.get(0).toString().contains("会话2的消息"));
        
        // 清理测试数据
        mySQLChatMemory.clear(conv1);
        mySQLChatMemory.clear(conv2);
        
        System.out.println("✅ 多会话隔离测试通过！");
    }
}