package com.example.mysite.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mysite.Entity.dto.Account;
import com.example.mysite.Mapper.AccountMapper;
import com.example.mysite.Service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Override
    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account=this.findAccountByNameOrEmail(username);
        if (account==null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User.withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }
}
