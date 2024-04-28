package com.duu.duurpc.proxy;

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

public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            String registryStr = RpcApplication.getRpcConfig().getRegistryConfig().getRegistry();
            Registry registry = RegistryFactory.getInstance(registryStr);
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            String serviceKey = serviceMetaInfo.getServiceKey();
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
            if (serviceMetaInfoList == null) {
                throw new RuntimeException("暂无服务");
            }
            String loadBalancer = RpcApplication.getRpcConfig().getLoadBalancer();
            LoadBalancer balancer = LoadBalancerFactory.getInstance(loadBalancer);
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo service = balancer.select(requestParams, serviceMetaInfoList);
            // ServiceMetaInfo service = serviceMetaInfoList.get(0);
            String serviceHost = service.getServiceHost();
            Integer servicePort = service.getServicePort();
            String retryStrategyStr = RpcApplication.getRpcConfig().getRetryStrategy();
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(retryStrategyStr);
            try {
                RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, servicePort, serviceHost)
                );
                return rpcResponse.getData();
            }catch (Exception e){
                String tolerantStrategyStr = RpcApplication.getRpcConfig().getTolerantStrategy();
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(tolerantStrategyStr);
                RpcResponse rpcResponse = tolerantStrategy.doTolerant(null, e);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}