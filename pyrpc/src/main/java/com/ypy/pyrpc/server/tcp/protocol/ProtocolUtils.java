package com.ypy.pyrpc.server.tcp.protocol;

import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.spi.serializer.Serializer;
import com.ypy.pyrpc.spi.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolUtils {
    private static Serializer getSerializerFromHeader(Protocol.Header header) {
        ProtocolSerializerEnum serializerEnum = ProtocolSerializerEnum.getByCode(header.getSerializer());
        if (serializerEnum == null) throw new RuntimeException("Unknown serializer " + header.getSerializer());
        return SerializerFactory.getInstance(serializerEnum.getKey());
    }

    /**
     * Protocol -> Buffer
     *
     * @param protocol
     * @return
     * @throws IOException
     */
    public static Buffer encode(Protocol<?> protocol) throws IOException {
        Buffer buffer = Buffer.buffer();
        if (protocol == null || protocol.getHeader() == null) return buffer;

        Protocol.Header header = protocol.getHeader();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        Serializer serializer = getSerializerFromHeader(header);

        byte[] bodyBytes = serializer.serialize(protocol.getBody());
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }

    public static Protocol<?> decode(Buffer buffer) throws IOException {
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConst.MAGIC) throw new RuntimeException("Unknown magic " + magic);

        Protocol.Header header = Protocol.Header.builder()
                .magic(magic)
                .version(buffer.getByte(1))
                .serializer(buffer.getByte(2))
                .type(buffer.getByte(3))
                .status(buffer.getByte(4))
                .requestId(buffer.getLong(5))
                .bodyLength(buffer.getInt(13)) // 5 + 8 (sizeof(long)) = 13
                .build();

        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength()); // 13 + 4(sizeof(int)) = 17
        Serializer serializer = getSerializerFromHeader(header);

        ProtocolTypeEnum typeEnum = ProtocolTypeEnum.getByCode(header.getType());
        if (typeEnum == null) throw new RuntimeException("Unknown type " + header.getType());

        switch (typeEnum) {
            case REQUEST:
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new Protocol<>(header, rpcRequest);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new Protocol<>(header, rpcResponse);
            case HEART_BEAT:
            case OTHERS:
                return new Protocol<>(header, null);
            default:
                throw new RuntimeException("Unknown type " + header.getType());
        }
    }
}
