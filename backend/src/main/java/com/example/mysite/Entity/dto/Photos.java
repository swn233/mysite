package com.example.mysite.Entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("photos")
@AllArgsConstructor
@NoArgsConstructor
public class Photos {
    @TableId(type = IdType.AUTO)
    private Integer id;          // 主键，自动增长
    private String title;        // 照片标题，非空
    private String author;       // 作者名，非空
    // 关键：显式指定数据库列名为 imageUrl
    @TableField(value = "imageUrl")
    private String imageUrl;     // 图片URL，非空
    private Integer likes;       // 点赞数，默认0
    private Integer favorites;   // 收藏数，默认0
    private Integer comments;    // 评论数，默认0
    private Boolean liked;       // 是否已点赞（false/true）
    private Boolean favorited;   // 是否已收藏（false/true）
}