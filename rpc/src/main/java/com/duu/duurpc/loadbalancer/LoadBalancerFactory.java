package com.duu.duurpc.loadbalancer;

import com.duu.duurpc.spi.SpiLoader;

/**
 * 负载均衡工厂
 * @author : duu
 * @data : 2024/3/26
 * @from ：https://github.com/0oHo0
 **/
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
