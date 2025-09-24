package com.kk.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import org.springframework.stereotype.Component;

@Component
public class TokenCounter {
    // 注册编码器 (线程安全)
    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();

    // 获取指定模型的编码器 (默认使用GPT-4/3.5的编码)
    private final Encoding encoding;

    public TokenCounter() {
        this.encoding = REGISTRY.getEncoding(EncodingType.CL100K_BASE); // GPT-3.5/4使用此编码
    }

    /**
     * 计算文本的token数量
     *
     * @param text 输入文本
     * @return token数量
     */
    public int countTokens(String text) {
        return encoding.countTokens(text);
    }

    /**
     * 按模型计算token数量
     *
     * @param modelName 模型名称 (如 "gpt-4", "gpt-3.5-turbo")
     * @param text      输入文本
     * @return token数量
     */
    public int countTokens(String modelName, String text) {
        Encoding modelEncoding = REGISTRY.getEncodingForModel(modelName).orElse(encoding);
        return modelEncoding.countTokens(text);
    }

}
