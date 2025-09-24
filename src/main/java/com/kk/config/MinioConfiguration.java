package com.kk.config;

import com.kk.properties.MinioProperties;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {

    @Resource
    private MinioProperties minioProperties;

    /**
     * 获取 MinioClient
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccesskey(), minioProperties.getSecretkey())
                .build();
    }
}

