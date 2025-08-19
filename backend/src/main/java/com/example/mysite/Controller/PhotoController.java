package com.example.mysite.Controller;

import com.example.mysite.Entity.dto.Photos;
import com.example.mysite.Service.PhotosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 照片管理控制器
 */
@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotosService photosService;

    /**
     * 上传照片
     * @param file 照片文件
     * @param title 照片标题
     * @param author 作者名
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("author") String author) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要上传的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "只能上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 上传照片
            Photos photo = photosService.uploadPhoto(file, title, author);
            
            // 构建完整的访问URL
            String photoUrl = photosService.getPhotoUrl(photo.getImageUrl());
            
            // 返回成功响应
            response.put("success", true);
            response.put("message", "照片上传成功");
            response.put("photo", photo);
            response.put("photoUrl", photoUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "照片上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取照片列表
     * @return 照片列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Photos>> getPhotoList() {
        List<Photos> photos = photosService.list();
        
        // 处理每张照片的URL
        for (Photos photo : photos) {
            // 将存储的文件名转换为完整的访问URL
            String photoUrl = photosService.getPhotoUrl(photo.getImageUrl());
            photo.setImageUrl(photoUrl);
        }
        
        return ResponseEntity.ok(photos);
    }

    /**
     * 获取照片详情
     * @param id 照片ID
     * @return 照片详情
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<Map<String, Object>> getPhotoDetail(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        Photos photo = photosService.getById(id);
        if (photo == null) {
            response.put("success", false);
            response.put("message", "照片不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // 处理照片URL
        String photoUrl = photosService.getPhotoUrl(photo.getImageUrl());
        photo.setImageUrl(photoUrl);
        
        response.put("success", true);
        response.put("photo", photo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除照片
     * @param id 照片ID
     * @return 删除结果
     */
    @DeleteMapping("/details/{id}")
    public ResponseEntity<Map<String, Object>> deletePhoto(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        boolean result = photosService.deletePhoto(id);
        if (result) {
            response.put("success", true);
            response.put("message", "照片删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "照片删除失败，可能照片不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}