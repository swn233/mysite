package com.example.mysite.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mysite.Entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);


}