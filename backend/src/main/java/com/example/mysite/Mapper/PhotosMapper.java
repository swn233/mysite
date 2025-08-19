package com.example.mysite.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mysite.Entity.dto.Photos;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PhotosMapper extends BaseMapper<Photos> {
}