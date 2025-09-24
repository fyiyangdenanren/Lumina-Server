package com.kk.repository;

import org.springframework.web.multipart.MultipartFile;

public interface IMilvusEmbeddingRepository {
    void insertRecord(Long conversationId, MultipartFile file);
}
