package com.duu.duurpc.fault.retry;

import com.duu.duurpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔
 * @author : duu
 * @data : 2024/3/27
 * @from ：https://github.com/0oHo0
 **/
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> build = RetryerBuilder.<RpcResponse>newBuilder()
                .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("第{}次重试", attempt.getAttemptNumber());
                    }
                }).build();
        return build.call(callable);
    }
}
