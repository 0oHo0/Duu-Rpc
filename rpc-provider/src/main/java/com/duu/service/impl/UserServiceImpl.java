package com.duu.service.impl;

import com.duu.duurpcspringbootstarter.annotation.RpcService;
import com.duu.model.User;
import com.duu.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author : duu
 * @data : 2024/2/23
 * @from ：https://github.com/0oHo0
 **/
@Service
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名"+user.getName());
        return user;
    }
}
