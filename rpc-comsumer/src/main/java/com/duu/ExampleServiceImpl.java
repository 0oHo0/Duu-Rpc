package com.duu;

import com.duu.duurpcspringbootstarter.annotation.RpcReference;
import com.duu.model.User;
import com.duu.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    public UserService userService;

    public void test() {
        User user = new User();
        user.setName("duu");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}