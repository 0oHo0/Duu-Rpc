package com.duu.duurpc.loadbalancer;

import cn.hutool.core.lang.tree.Tree;
import com.duu.duurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡
 *
 * @author : duu
 * @data : 2024/3/26
 * @from ：https://github.com/0oHo0
 **/
public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                String address = serviceMetaInfo.getServiceAddress();
                int hash = getHash(address + "#" + i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }
        int request = getHash(requestParams);
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(request);
        if (entry == null) {
            return virtualNodes.firstEntry().getValue();
        }
        return entry.getValue();

    }

    /**
     * Hash算法
     *
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
