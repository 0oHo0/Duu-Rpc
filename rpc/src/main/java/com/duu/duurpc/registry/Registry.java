package com.duu.duurpc.registry;

import com.duu.duurpc.config.RegistryConfig;
import com.duu.duurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author : duu
 * @data : 2024/3/23
 * @from ：https://github.com/0oHo0
 **/
public interface Registry {

    void init(RegistryConfig registryConfig);

    void registry(ServiceMetaInfo serviceMetaInfo);

    void unRegistry(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey) throws ExecutionException, InterruptedException;

    void destroy();

    /*
    * 服务心跳检测
    */
    void heartBeat();

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey
     */
    void watch(String serviceNodeKey);
}
