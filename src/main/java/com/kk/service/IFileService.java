package com.kk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kk.domain.po.File;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author kk
 * @since 2025-08-02
 */
public interface IFileService extends IService<File> {

    String uploadFile(Long conversationId, MultipartFile file);

    void removeFile(String url);

    void downLoadFile(String fileName, String bucketName, HttpServletResponse response);

}
