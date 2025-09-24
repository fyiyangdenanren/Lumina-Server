package com.kk.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file")
public class File {

    @TableId(value = "file_id", type = IdType.ASSIGN_ID)
    private Long fileId;

    @TableField("user_id")
    private Long userId;

    @TableField("conversation_id")
    private Long conversationId;

    @TableField("file_hash")
    private String fileHash;

    @TableField("url")
    private String url;

    @TableField("size")
    private Long size;

    @TableField("mime_type")
    private String mimeType;

    @TableField(value = "upload_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime uploadTime;
}
