package com.duu.duurpcspringbootstarter.annotation;

import com.duu.duurpc.constant.LoadBalancerKeys;
import com.duu.duurpc.constant.RpcConstant;
import com.duu.duurpc.fault.retry.RetryStrategyKeys;
import com.duu.duurpc.fault.tolerant.TolerantStrategyKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : duu
 * @data : 2024/3/28
 * @from ：https://github.com/0oHo0
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    //服务接口
    Class<?> interfaceClass() default void.class;

    //服务版本号
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    //重试机制
    String retryStrategy() default RetryStrategyKeys.NO;

    //负载均衡
    String loadBalancer() default LoadBalancerKeys.CONSISTENT_HASH;


    String mock() default "false";

    //容错
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;
}
