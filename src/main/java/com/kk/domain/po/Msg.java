package com.kk.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName(value = "message", autoResultMap = true)
public class Msg {

    @TableId(value = "message_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long messageId;

    @TableField("conversation_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long conversationId;

    @TableField(value = "message_type")
    private String messageType;

    @TableField("content")
    private String content;

    @TableField("tokens")
    private Integer tokens = 0;

    @TableField("parent_message_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentMessageId;

    @TableField(value = "attachments")
    private String attachments;

    @TableField(value = "metadata", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime createdTime;

}