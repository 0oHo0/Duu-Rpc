package com.duu.duurpc.loadbalancer;

import com.duu.duurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * @description: 随机负载均衡
 * @author: duu
 * @date: 2024/3/26 16:28
 **/
public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if (size == 0) {
            return null;
        }
        // 只有 1 个服务，不用随机
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}