package com.example.mysite.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mysite.Entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
