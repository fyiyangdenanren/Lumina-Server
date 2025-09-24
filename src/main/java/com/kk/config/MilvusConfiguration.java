package com.kk.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kk.constants.MilvusConstant.*;

@Configuration
@RequiredArgsConstructor
public class MilvusConfiguration {

    @Bean
    public VectorStore vectorStore(MilvusServiceClient milvusClient, OpenAiEmbeddingModel embeddingModel) {
        return MilvusVectorStore.builder(milvusClient, embeddingModel)
                .collectionName(COLLECTION_NAME)
                .databaseName(DATABASE_NAME)
                .embeddingDimension(VECTOR_DIM)
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .initializeSchema(true)
                .build();
    }

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withHost(HOST)
                .withPort(PORT)
                .withAuthorization(USERNAME, PASSWORD)
                .withDatabaseName(DATABASE_NAME)
                .build());
    }
}
