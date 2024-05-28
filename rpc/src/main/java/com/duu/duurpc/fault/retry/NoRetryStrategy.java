package com.duu.duurpc.fault.retry;

import com.duu.duurpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * @description: 不重试
 * @author: duu
 * @date: 2024/3/27 20:31
 **/
public class NoRetryStrategy implements RetryStrategy {

    /**
     * 重试
     *
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}

