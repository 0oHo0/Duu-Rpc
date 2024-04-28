package com.duu;

import com.duu.duurpc.config.RegistryConfig;
import com.duu.duurpc.config.RpcConfig;
import com.duu.duurpc.constant.RpcConstant;
import com.duu.duurpc.registry.EtcdRegistry;
import com.duu.duurpc.registry.Registry;
import com.duu.duurpc.registry.RegistryFactory;
import com.duu.duurpc.utils.ConfigUtils;

/**
 * @author : duu
 * @data : 2024/3/21
 * @from ：https://github.com/0oHo0
 **/

public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        System.out.println("rpc init, config = {}"+ newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        System.out.println("registry init, config = {}"+ registryConfig);
        //注册关闭服务操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
