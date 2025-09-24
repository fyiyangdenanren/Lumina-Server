package com.kk.repository.impl;

import com.kk.etl.DocumentETLPipeline;
import com.kk.repository.IMilvusEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MilvusEmbeddingRepository implements IMilvusEmbeddingRepository {
    private final DocumentETLPipeline documentETLPipeline;
    private final VectorStore vectorStore;

    @Override
    public void insertRecord(final Long conversationId, @NotNull final MultipartFile file) {
        // 1.解析文件
        Resource resource = file.getResource();
        // 2.处理文件
        List<Document> documents = documentETLPipeline.processBatchDocuments(List.of(resource));
        // 3.封装metadata
        documents.forEach(document ->
                document.getMetadata().put("conversation_id", conversationId.toString())
        );
        // 4.分批插入数据(每批不超过10个文档)
        int batchSize = 10;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            List<Document> batch = documents.subList(i, end);
            vectorStore.add(batch);
        }
    }

    // TODO 删除数据

}
