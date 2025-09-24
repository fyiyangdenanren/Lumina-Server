package com.kk.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "conversation", autoResultMap = true)
public class Conversation {

    @TableId(value = "conversation_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long conversationId;

    @TableField("user_id")
    private Long userId;

    @TableField("title")
    private String title = "New Chat";

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted = 0;

    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime createdTime;
}
