package com.duu.duurpc.config;

import com.duu.duurpc.constant.LoadBalancerKeys;
import com.duu.duurpc.fault.retry.RetryStrategyKeys;
import com.duu.duurpc.fault.tolerant.TolerantStrategyKeys;
import com.duu.duurpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * @author : duu
 * @data : 2024/3/21
 * @from ：https://github.com/0oHo0
 **/
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "duu";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    private RegistryConfig registryConfig;

    private String serializer = SerializerKeys.JDK;

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;

}
