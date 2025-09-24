package com.kk.domain.po;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    /**
     * 文件ID
     */
    private Long fileId;
    
    /**
     * 原始文件名
     */
    private String originalName;
    
    /**
     * 文件大小（字节）
     */
    private Long sizeBytes;
    
    /**
     * MIME类型
     */
    private String mimeType;
    
    /**
     * 存储路径
     */
    private String storagePath;
}