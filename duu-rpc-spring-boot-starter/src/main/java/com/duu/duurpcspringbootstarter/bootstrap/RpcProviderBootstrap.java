package com.duu.duurpcspringbootstarter.bootstrap;

import com.duu.RpcApplication;
import com.duu.duurpc.config.RegistryConfig;
import com.duu.duurpc.config.RpcConfig;
import com.duu.duurpc.model.ServiceMetaInfo;
import com.duu.duurpc.registry.LocalRegistry;
import com.duu.duurpc.registry.Registry;
import com.duu.duurpc.registry.RegistryFactory;
import com.duu.duurpcspringbootstarter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

/**
 * @author : duu
 * @data : 2024/3/28
 * @from ：https://github.com/0oHo0
 **/
public class RpcProviderBootstrap implements BeanPostProcessor{
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            Class<?> interfaceClass = rpcService.interfaceClass();
            if (interfaceClass==void.class){
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String name = interfaceClass.getName();
            String version = rpcService.serviceVersion();
            LocalRegistry.register(name, beanClass);
            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(name);
            serviceMetaInfo.setServiceVersion(version);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.registry(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(name + " 服务注册失败", e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
