package com.duu.duurpc.fault.retry;

import com.duu.duurpc.registry.EtcdRegistry;
import com.duu.duurpc.registry.Registry;
import com.duu.duurpc.spi.SpiLoader;

public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认注册中心
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * @description: 获取重试策略实例
     * @author: duu
     * @date: 2024/3/23 16:58
     * @param: key
     * @return: Register
     **/
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
