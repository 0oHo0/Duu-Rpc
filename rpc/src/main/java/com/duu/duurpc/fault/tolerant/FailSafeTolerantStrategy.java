package com.duu.duurpc.fault.tolerant;

import com.duu.duurpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author : duu
 * @data : 2024/3/28
 * @from ：https://github.com/0oHo0
 **/
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理异常", e);
        return new RpcResponse();
    }
}
