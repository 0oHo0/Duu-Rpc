package com.duu.duurpc.bootstrap;

import com.duu.RpcApplication;
import com.duu.duurpc.config.RegistryConfig;
import com.duu.duurpc.config.RpcConfig;
import com.duu.duurpc.model.ServiceMetaInfo;
import com.duu.duurpc.model.ServiceRegisterInfo;
import com.duu.duurpc.registry.LocalRegistry;
import com.duu.duurpc.registry.Registry;
import com.duu.duurpc.registry.RegistryFactory;
import com.duu.duurpc.sever.tcp.VertxTcpServer;

import java.util.List;

/**
 * @author : duu
 * @data : 2024/3/28
 * @from ：https://github.com/0oHo0
 **/
public class ProviderBootstrap {
    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.registry(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        // 启动服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
