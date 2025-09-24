package com.kk.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kk.constants.HttpStatus;
import com.kk.domain.po.File;
import com.kk.exception.CustomException;
import com.kk.exception.ServerException;
import com.kk.mapper.FileMapper;
import com.kk.properties.MinioProperties;
import com.kk.repository.impl.MilvusEmbeddingRepository;
import com.kk.service.IFileService;
import com.kk.utils.UserContextHolder;
import io.minio.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import static com.kk.utils.FileUtil.getExtension;
import static com.kk.utils.FileUtil.getMimeType;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author kk
 * @since 2025-08-02
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final MilvusEmbeddingRepository milvusEmbeddingRepository;

    @Override
    @Transactional
    public String uploadFile(final Long conversationId, final MultipartFile file) {
        if (file == null || file.getSize() == 0) {
            throw new CustomException("上传失败，请选择文件", HttpStatus.BAD_REQUEST);
        }
        try {
            // 获取当前用户
            String userId = UserContextHolder.getUserId();
            // 创建桶
            String bucketName = createBucket();
            // 文件哈希值
            String md5Hash = DigestUtils.md5Hex(file.getInputStream());
            // 获取当前时间hh:mm:ss
            String nowTime = DateUtil.format(new Date(), "HH:mm:ss");
            // 设置存储对象文件名
            String objectName = userId + "/" + nowTime + "/" + md5Hash + getExtension(file);
            // 上传文件到minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            String url = minioProperties.getEndpoint() + "/" + bucketName + "/" + objectName;
            if (this.getOne(new LambdaQueryWrapper<File>().eq(File::getUrl, url)) != null) {
                return url;
            }
            // 保存文件到mysql
            saveFile(file, conversationId, userId, md5Hash, url);
            // 文件向量化存储
            try {
                milvusEmbeddingRepository.insertRecord(conversationId, file);
            } catch (Exception e) {
                throw new ServerException("文件嵌入失败" + e, HttpStatus.ERROR);
            }
            return url;
        } catch (Exception e) {
            throw new ServerException(e.toString(), HttpStatus.ERROR);
        }
    }

    @Override
    public void removeFile(final String url) {
        try {
            // 解析URL获取bucketName和objectName
            String[] parts = url.split("/");
            String bucketName = parts[3];
            String objectName = String.join("/", Arrays.copyOfRange(parts, 4, parts.length));
            // 判断桶是否存在
            boolean res = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (res) {
                // 删除文件
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
            }
            // 删除mysql中数据
            if (this.getOne(new LambdaQueryWrapper<File>().eq(File::getUrl, url)) != null) {
                this.remove(new LambdaQueryWrapper<File>().eq(File::getUrl, url));
            }
        } catch (Exception e) {
            throw new ServerException(e.toString(), HttpStatus.ERROR);
        }
    }

    @Override
    public void downLoadFile(final String fileName, final String bucketName, final HttpServletResponse response) {

        if (StrUtil.isBlank(fileName)) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            try {
                OutputStream ps = response.getOutputStream();
                ps.write("文件下载失败".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new ServerException(e.toString(), HttpStatus.ERROR);
            }
            return;
        }

        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode(fileName.substring(fileName.lastIndexOf("/") + 1), StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");

        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buf = new byte[1024];
            int length;
            while ((length = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, length);
            }
        } catch (Exception e) {
            try {
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                OutputStream ps = response.getOutputStream();
                ps.write("文件下载失败".getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioException) {
                throw new ServerException(ioException.toString(), HttpStatus.ERROR);
            }
        }
    }

    /**
     * 创建桶
     */
    @SneakyThrows
    public String createBucket() {
        // 获取当前日期
        String buckName = DateUtil.format(new Date(), "yyyy-MM-dd");
        // 如果不存在就创建
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(buckName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(buckName).build());
        }
        return buckName;
    }

    /**
     * 封装File对象
     */
    private void saveFile(@NotNull final MultipartFile file, final Long conversationId, final String userId, final String md5Hash, final String url) {
        // 封装File
        File f = new File();
        f.setUserId(Long.valueOf(userId));
        f.setFileHash(md5Hash);
        f.setConversationId(conversationId);
        f.setSize(file.getSize());
        f.setMimeType(getMimeType(getExtension(file)));
        f.setUrl(url);
        this.save(f);
    }
}
