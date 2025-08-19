package com.example.mysite.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mysite.Entity.dto.Photos;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotosService extends IService<Photos> {
    /**
     * 上传照片
     * @param file 照片文件
     * @param title 照片标题
     * @param author 作者名
     * @return 照片实体
     */
    Photos uploadPhoto(MultipartFile file, String title, String author) throws IOException;
    
    /**
     * 删除照片
     * @param photoId 照片ID
     * @return 是否删除成功
     */
    boolean deletePhoto(Integer photoId);
    
    /**
     * 获取照片访问URL
     * @param fileName 文件名
     * @return 完整的访问URL
     */
    String getPhotoUrl(String fileName);
}