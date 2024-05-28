package com.duu;

import com.duu.duurpc.bootstrap.ConsumerBootstrap;
import com.duu.duurpc.proxy.ServiceProxyFactory;
import com.duu.duurpcspringbootstarter.annotation.RpcReference;
import com.duu.model.User;
import com.duu.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl implements CommandLineRunner {

    @RpcReference
    public UserService userService;


    public void test() {
        User user = new User();
        user.setName("duu");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

    public static void main(String[] args) {
        // 服务提供者初始化
        ConsumerBootstrap.init();
        ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactory();
        // 动态代理
        UserService userService = serviceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("我是聪！！！！");
        // 调用
        long before = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            User newUser = userService.getUser(user);
            if (newUser != null) {
                System.out.println("我是name啊"+newUser.getName());
            } else {
                System.out.println("user == null");
            }
        }
        long after = System.currentTimeMillis();
        System.out.println(after-before);
        long number = userService.getNumber();
        System.out.println(number);

    }

    @Override
    public void run(String... args) throws Exception {
        test();
    }
}