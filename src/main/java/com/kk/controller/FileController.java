package com.kk.controller;

import com.kk.domain.po.R;
import com.kk.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final IFileService fileService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam(value = "conversationId") Long conversationId, @RequestParam(value = "file") MultipartFile file) {
        String url = fileService.uploadFile(conversationId, file);
        return R.ok(url);
    }

    /**
     * 文件删除
     */
    @DeleteMapping("/delete")
    public R<Void> delete(@RequestParam(value = "url") String url) {
        fileService.removeFile(url);
        return R.ok();
    }
}
