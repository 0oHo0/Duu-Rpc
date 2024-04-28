package com.duu.duurpc.model;

import com.duu.duurpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : duu
 * @data : 2024/2/24
 * @from ：https://github.com/0oHo0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    String serviceName;
    String methodName;
    Class<?>[] parameterTypes;
    Object[] args;
    String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
}
