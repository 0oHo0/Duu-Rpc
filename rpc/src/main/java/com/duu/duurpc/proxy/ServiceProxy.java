package com.duu.duurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.duu.RpcApplication;
import com.duu.duurpc.constant.RpcConstant;
import com.duu.duurpc.fault.retry.RetryStrategy;
import com.duu.duurpc.fault.retry.RetryStrategyFactory;
import com.duu.duurpc.fault.tolerant.TolerantStrategy;
import com.duu.duurpc.fault.tolerant.TolerantStrategyFactory;
import com.duu.duurpc.loadbalancer.LoadBalancer;
import com.duu.duurpc.loadbalancer.LoadBalancerFactory;
import com.duu.duurpc.model.RpcRequest;
import com.duu.duurpc.model.RpcResponse;
import com.duu.duurpc.model.ServiceMetaInfo;
import com.duu.duurpc.protocol.*;
import com.duu.duurpc.registry.Registry;
import com.duu.duurpc.registry.RegistryFactory;
import com.duu.duurpc.serializer.JdkSerializer;
import com.duu.duurpc.serializer.Serializer;
import com.duu.duurpc.serializer.SerializerFactory;
import com.duu.duurpc.sever.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServiceProxy implements InvocationHandler {
    /**
     * 调用远程服务的方法。
     *
     * @param proxy 代理对象，表示正在执行代理操作的对象。
     * @param method 被调用的方法信息，包含方法名、参数类型等。
     * @param args 方法调用的参数数组。
     * @return 返回远程服务调用的结果。
     * @throws Throwable 如果调用过程中发生异常，则抛出。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建RPC请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        // 通过注册中心获取服务提供者信息，并选择一个服务进行调用
        String registryStr = RpcApplication.getRpcConfig().getRegistryConfig().getRegistry();
        Registry registry = RegistryFactory.getInstance(registryStr);
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务");
        }
        String loadBalancer = RpcApplication.getRpcConfig().getLoadBalancer();
        LoadBalancer balancer = LoadBalancerFactory.getInstance(loadBalancer);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo service = balancer.select(requestParams, serviceMetaInfoList);
        String serviceHost = service.getServiceHost();
        Integer servicePort = service.getServicePort();
        // 发起远程调用，处理异常情况
        RpcResponse rpcResponse;
        try {
            String retryStrategyStr = RpcApplication.getRpcConfig().getRetryStrategy();
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(retryStrategyStr);
            rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, servicePort, serviceHost)
            );
        } catch (Exception e) {
            String tolerantStrategyStr = RpcApplication.getRpcConfig().getTolerantStrategy();
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(tolerantStrategyStr);
            HashMap<String,Object> map = new HashMap<String,Object>() {{
                put("serviceList", serviceMetaInfoList);
                put("selectedService", service);
                put("request", rpcRequest);
            }};
            rpcResponse = tolerantStrategy.doTolerant(map, e);
        }
        // 返回远程调用的结果数据
        return rpcResponse.getData();
    }
}