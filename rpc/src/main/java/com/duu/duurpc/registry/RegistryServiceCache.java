package com.duu.duurpc.registry;

import com.duu.duurpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author : duu
 * @data : 2024/3/24
 * @from ：https://github.com/0oHo0
 **/
public class RegistryServiceCache {

    //服务缓存
    List<ServiceMetaInfo> serviceMetaCache;

    /**
     * @description: 写缓存
     * @author: duu
     * @date: 2024/3/24 15:06
     * @param: serviceMetaInfoList
     * @return: void
     **/
    void writeCache(List<ServiceMetaInfo> serviceMetaInfoList) {
        this.serviceMetaCache = serviceMetaInfoList;
    }

    List<ServiceMetaInfo> readCache() {
        return this.serviceMetaCache;
    }
    //清空缓存
    void clearCache() {
        this.serviceMetaCache = null;
    }
}
