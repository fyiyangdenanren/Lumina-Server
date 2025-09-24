package com.kk.utils;


import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    /**
     * 根据MultipartFile对象获取其文件扩展名
     *
     * @param multipartFile MultipartFile对象
     */
    @NotNull
    public static String getExtension(@NotNull MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        assert filename != null;
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 根据扩展名获取文件类型（mimetype）
     *
     * @param extension 文件的扩展名，如：“.png”
     */
    public static String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        // 根据文件扩展名获取文件类型
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }
}
