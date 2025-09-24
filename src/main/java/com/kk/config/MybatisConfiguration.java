package com.kk.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfiguration{

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 1.创建分页插件
        PaginationInnerInterceptor pg = new PaginationInnerInterceptor(DbType.MYSQL);
        // 2.设置单页最大数量
        pg.setMaxLimit(1000L);
        mybatisPlusInterceptor.addInnerInterceptor(pg);
        return mybatisPlusInterceptor;
    }

}
