package com.kk.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntentDecision {
    /**
     * 是否使用 rag
     */
    private Boolean rag;
    /**
     * 是否使用 webSearch
     */
    private Boolean webSearch;
    /**
     * 是否生成图表
     */
    private Boolean chart;
    /**
     * 意图标签
     */
    private String intentLabel;
    /**
     * 置信度
     */
    private Double confidence;
}