package com.kk.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class ConversationVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long conversationId;
    private String title;
}
