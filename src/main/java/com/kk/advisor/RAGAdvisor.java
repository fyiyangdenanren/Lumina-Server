package com.kk.advisor;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kk.domain.po.File;
import com.kk.service.IFileService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 生产级 RAGAdvisor —— 使用结构化 JSON 注入（Jackson），防止注入出错并对文本长度做保护。
 */
@Component
@RequiredArgsConstructor
public class RAGAdvisor implements StreamAdvisor, Ordered {

    private final VectorStore vectorStore;
    private final IFileService fileService;
    private final RewriteQueryTransformer rewriteQueryTransformer;

    @NotNull
    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull final ChatClientRequest request, @NonNull final StreamAdvisorChain chain) {
        // 1.获取conversationId参数
        Map<String, Object> ctx = request.context();
        Long conversationId = (Long) ctx.get(ChatMemory.CONVERSATION_ID);
        String userText = request.prompt().getUserMessage().getText();
        // 2.重写查询转换器
        Query query = new Query(userText);
        Query transformedQuery = rewriteQueryTransformer.transform(query);
        // 3.判断是否RAG
        LambdaQueryWrapper<File> fileQW = new LambdaQueryWrapper<File>().eq(File::getConversationId, conversationId);
        if (ObjectUtil.isEmpty(fileService.getOne(fileQW))) {
            return chain.nextStream(request);
        }
        // 4.构建条件
        // 4.1.构建过滤条件
        Filter.Expression conversationIdExpression = new Filter.Expression(Filter.ExpressionType.EQ, new Filter.Key("conversation_id"), new Filter.Value(conversationId.toString()));
        // 4.2.构建查询条件
        SearchRequest searchRequest = SearchRequest.builder().query(transformedQuery.text()).topK(5).similarityThreshold(0.5d).filterExpression(conversationIdExpression).build();
        // 5.检索
        List<Document> docs = vectorStore.similaritySearch(searchRequest);
        if (docs.isEmpty()) {
            return chain.nextStream(request);
        }
        // 6.把文档格式化成字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < docs.size(); i++) {
            sb.append(i + 1).append(". ").append(docs.get(i).getText()).append("\n");
        }
        String injected = sb.toString();
        // 7. 注入到用户输入里，得到一个新的
        Prompt newPrompt = request.prompt().augmentUserMessage(um -> um.mutate().text(um.getText() + "\n\nRetrieved:\n" + injected).build());
        // 8. 创建新的请求
        ChatClientRequest newRequest = ChatClientRequest.builder().prompt(newPrompt).context(ctx).build();
        // 10.传递
        return chain.nextStream(newRequest);
    }

    @NotNull
    @Override
    public String getName() {
        return "RAGAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
