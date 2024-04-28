package com.duu;

import com.duu.duurpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : duu
 * @data : 2024/2/23
 * @from ：https://github.com/0oHo0
 **/
@EnableRpc(needServer = false)
@SpringBootApplication
public class SpringbootConsumerApplication {

    public static void main(String[] args) {
        //简易
//        User user = new User();
//        user.setName("duu");
//        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
//        User userResponse = userService.getUser(user);
//        System.out.println(userResponse);
//        ConsumerBootstrap.init();
//        User user = new User();
//        user.setName("duu");
//        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
//        User userResponse = userService.getUser(user);
//        System.out.println(userResponse);
        SpringApplication.run(SpringbootConsumerApplication.class, args);
    }
}