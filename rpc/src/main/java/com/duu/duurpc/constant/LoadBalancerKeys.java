package com.duu.duurpc.constant;
/**
 * @description: 负载均衡键名常量
 * @author: duu
 * @date: 2024/3/26 17:30
 **/
public interface LoadBalancerKeys {

    /**
     * 轮询
     */
    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";

}