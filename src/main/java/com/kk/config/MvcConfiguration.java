package com.kk.config;


import com.kk.interceptor.JwtInterceptor;
import com.kk.interceptor.LoginInterceptor;
import com.kk.properties.JwtProperties;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JwtProperties jwtProperties;

    @Override
    public void addInterceptors(@NotNull final InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/login/**",
                        "/register/**",
                        "/user/send/**"
                )
                .order(1);
        registry.addInterceptor(new JwtInterceptor(jwtProperties,stringRedisTemplate))
                .addPathPatterns("/**")
                .order(0);

    }

    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition");
    }
}
