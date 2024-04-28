package com.duu.duurpc.proxy;

import com.duu.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * @author : duu
 * @data : 2024/3/21
 * @from ：https://github.com/0oHo0
 **/
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass){
        if (RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }

    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy());
    }
}
