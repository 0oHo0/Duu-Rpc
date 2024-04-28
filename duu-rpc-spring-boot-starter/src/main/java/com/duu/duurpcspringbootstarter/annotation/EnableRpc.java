package com.duu.duurpcspringbootstarter.annotation;

import com.duu.duurpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.duu.duurpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.duu.duurpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于全局标识项目需要引入 RPC 框架、执行初始化方法。
 * @author : duu
 * @data : 2024/3/28
 * @from ：https://github.com/0oHo0
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动 server
     *
     * @return
     */
    boolean needServer() default true;
}
