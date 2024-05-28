package com.duu.duurpc.sever.tcp;

import com.duu.duurpc.model.RpcRequest;
import com.duu.duurpc.model.RpcResponse;
import com.duu.duurpc.protocol.ProtocolMessage;
import com.duu.duurpc.protocol.ProtocolMessageDecoder;
import com.duu.duurpc.protocol.ProtocolMessageEncoder;
import com.duu.duurpc.protocol.ProtocolMessageTypeEnum;
import com.duu.duurpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接受请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            RpcResponse rpcResponse = new RpcResponse();
            try {
                String serviceName = rpcRequest.getServiceName();
                String methodName = rpcRequest.getMethodName();
                Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
                Object[] args = rpcRequest.getArgs();
                Class<?> implClass = LocalRegistry.get(serviceName);
                Method method = implClass.getMethod(methodName, parameterTypes);
                Object result = method.invoke(implClass.newInstance(), args);
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
            }

            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer buffer1 = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(buffer1);
            } catch (IOException e) {
                throw new RuntimeException("协议编码失败");
            }
        });
        netSocket.handler(bufferHandlerWrapper);
    }
}