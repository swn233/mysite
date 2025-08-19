package com.example.mysite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class FileConfig implements WebMvcConfigurer {
    // 从配置文件中读取本地文件存储路径
    @Value("${file.upload.path}")
    private String uploadPath;

    // 从配置文件中读取前端访问路径前缀
    @Value("${file.access.path}")
    private String accessPath;

    /**
     * 配置静态资源映射：将本地文件路径映射为HTTP可访问的URL
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在，不存在则自动创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 递归创建目录（包括父目录）
        }

        // 关键：建立访问路径与本地路径的映射关系
        // 格式：访问URL = accessPath + 文件名 → 对应本地路径 = uploadPath + 文件名
        registry.addResourceHandler(accessPath + "**") // 匹配以 /api/photos/ 开头的所有URL
                .addResourceLocations("file:" + uploadPath); // 映射到本地的上传目录
    }

}