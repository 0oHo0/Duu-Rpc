package com.duu.duurpc.fault.tolerant;

import com.duu.duurpc.model.RpcResponse;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.*;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author : duu
 * @data : 2024/3/28
 * @from ：https://github.com/0oHo0
 **/
public class FailFastTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错", e);
    }
}
