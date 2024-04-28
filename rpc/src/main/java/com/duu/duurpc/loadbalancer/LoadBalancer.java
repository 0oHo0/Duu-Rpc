package com.duu.duurpc.loadbalancer;

import com.duu.duurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**负载均衡器（消费端使用）
 * @author : duu
 * @data : 2024/3/26
 * @from ：https://github.com/0oHo0
 **/
public interface LoadBalancer {

    /**
     * @description: 负载均衡选择服务
     * @author: duu
     * @date: 2024/3/26 15:52
     * @param requestParams
     * @param serviceMetaInfoList
     * @return ServiceMetaInfo
     **/
    ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
