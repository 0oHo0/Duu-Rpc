package com.duu.duurpc.registry;

import com.duu.duurpc.spi.SpiLoader;

public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * @description:获取实例
     * @author: duu
     * @date: 2024/3/23 16:58
     * @param: key
     * @return: Register
     **/
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}
