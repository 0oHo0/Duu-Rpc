package com.duu.duurpc.protocol;

import com.duu.duurpc.model.RpcRequest;
import com.duu.duurpc.model.RpcResponse;
import com.duu.duurpc.serializer.Serializer;
import com.duu.duurpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * @author : duu
 * @data : 2024/3/25
 * @from ：https://github.com/0oHo0
 **/
public class ProtocolMessageDecoder {

    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        byte version = buffer.getByte(1);
        byte serializer = buffer.getByte(2);
        byte type = buffer.getByte(3);
        byte status = buffer.getByte(4);
        long requestId = buffer.getLong(5);
        int bodyLength = buffer.getInt(13);
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerializer(serializer);
        header.setType(type);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setBodyLength(bodyLength);
        // 解决粘包问题，只读指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(serializer);
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializerInstance = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnumByKey(type);
        if (typeEnum == null) {
            throw new RuntimeException("类型不存在");
        }
        switch (typeEnum) {
            case REQUEST:
                RpcRequest rpcRequest = serializerInstance.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, rpcRequest);
            case RESPONSE:
                RpcResponse response = serializerInstance.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }


    }
}
