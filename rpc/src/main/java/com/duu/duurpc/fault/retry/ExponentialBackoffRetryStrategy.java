package com.duu.duurpc.fault.retry;

import com.duu.duurpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Duu
 * @description
 * @date 2024/05/20 21:47
 * @from https://github.com/0oHo0
 **/
@Slf4j
public class ExponentialBackoffRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return RetryerBuilder.<RpcResponse>newBuilder().withWaitStrategy(WaitStrategies.exponentialWait(1000, 10,
                        TimeUnit.SECONDS))
                .retryIfExceptionOfType(Exception.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}, 距离第一次重试的延迟 {} 毫秒",
                                attempt.getAttemptNumber() - 1, attempt.getAttemptNumber() - 1 == 0 ? 0 :
                                        attempt.getDelaySinceFirstAttempt());
                    }
                }).build().call(callable);

    }
}