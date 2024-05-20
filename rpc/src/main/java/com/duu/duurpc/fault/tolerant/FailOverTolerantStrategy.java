package com.duu.duurpc.fault.tolerant;

import cn.hutool.core.collection.CollUtil;
import com.duu.duurpc.constant.LoadBalancerKeys;
import com.duu.duurpc.loadbalancer.LoadBalancer;
import com.duu.duurpc.loadbalancer.LoadBalancerFactory;
import com.duu.duurpc.model.RpcRequest;
import com.duu.duurpc.model.RpcResponse;
import com.duu.duurpc.model.ServiceMetaInfo;
import com.duu.duurpc.sever.tcp.VertxTcpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Duu
 * @description
 * @date 2024/05/17 16:49
 * @from https://github.com/0oHo0
 **/
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws ExecutionException, InterruptedException {
        List<ServiceMetaInfo> serviceList = (List<ServiceMetaInfo>) context.get("serviceList");
        ServiceMetaInfo selectedService = (ServiceMetaInfo) context.get("selectedService");
        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");
        serviceList.remove(selectedService);
        if (CollUtil.isNotEmpty(serviceList)) {
            // 重新调用其他服务
            // 负载均衡
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(LoadBalancerKeys.ROUND_ROBIN);
            ServiceMetaInfo service = loadBalancer.select(requestParams, serviceList);
            return VertxTcpClient.doRequest(rpcRequest, service.getServicePort(), service.getServiceHost());
        }else {
            throw new RuntimeException("找不到其他可用服务");
        }
    }
}