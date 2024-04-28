package com.duu.service;

import com.duu.model.User;

/**
 * @author : duu
 * @data : 2024/2/23
 * @from ：https://github.com/0oHo0
 **/
public interface UserService {
    User getUser(User user);

    default short getNumber() {
        return 1;
    }
}
