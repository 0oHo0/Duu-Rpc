package com.duu.duurpc.fault.tolerant;

import com.duu.duurpc.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 容错策略
 * @author : duu
 * @data : 2024/3/28
 * @from ：https://github.com/0oHo0
 **/
public interface TolerantStrategy {

    public RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
