package com.example.mysite.Utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUtil {

    /**
     * 上传文件到指定目录
     * @param file 上传的文件
     * @param uploadDir 上传目录
     * @return 文件访问路径
     * @throws IOException 文件操作异常
     */
    public static String saveFile(MultipartFile file, String uploadDir) throws IOException {
        // 创建上传目录
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path filePath = Paths.get(uploadDir, newFileName);
        Files.copy(file.getInputStream(), filePath);
        
        return newFileName;
    }
    
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}