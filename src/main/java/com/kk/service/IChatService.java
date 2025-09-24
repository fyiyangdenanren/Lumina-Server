package com.kk.service;

import reactor.core.publisher.Flux;

public interface IChatService {
    Flux<String> chatStream(Long conversationId, String prompt);

    String abortChat(Long conversationId);
}
