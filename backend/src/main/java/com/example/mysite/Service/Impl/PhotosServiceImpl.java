package com.example.mysite.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mysite.Entity.dto.Photos;
import com.example.mysite.Mapper.PhotosMapper;
import com.example.mysite.Service.PhotosService;
import com.example.mysite.Utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class PhotosServiceImpl extends ServiceImpl<PhotosMapper, Photos> implements PhotosService {

    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.access.path}")
    private String accessPath;
    
    /**
     * 上传照片
     * @param file 照片文件
     * @param title 照片标题
     * @param author 作者名
     * @return 照片实体
     */
    @Override
    public Photos uploadPhoto(MultipartFile file, String title, String author) throws IOException {
        // 保存文件到服务器文件系统
        String fileName = FileUtil.saveFile(file, uploadPath);
        
        // 创建照片实体并保存到数据库
        Photos photo = new Photos();
        photo.setTitle(title);
        photo.setAuthor(author);
        photo.setImageUrl(fileName); // 保存文件名，而不是完整URL
        photo.setLikes(0);
        photo.setFavorites(0);
        photo.setComments(0);
        photo.setLiked(false);
        photo.setFavorited(false);
        
        // 保存到数据库
        this.save(photo);
        
        return photo;
    }
    
    /**
     * 删除照片
     * @param photoId 照片ID
     * @return 是否删除成功
     */
    @Override
    public boolean deletePhoto(Integer photoId) {
        // 查询照片信息
        Photos photo = this.getById(photoId);
        if (photo == null) {
            return false;
        }
        
        // 删除文件系统中的照片文件
        String filePath = uploadPath + photo.getImageUrl();
        boolean fileDeleted = FileUtil.deleteFile(filePath);
        
        // 删除数据库记录
        boolean recordDeleted = this.removeById(photoId);
        
        return fileDeleted && recordDeleted;
    }
    
    /**
     * 获取照片访问URL
     * @param fileName 文件名
     * @return 完整的访问URL
     */
    @Override
    public String getPhotoUrl(String fileName) {
        return accessPath + fileName;
    }
}