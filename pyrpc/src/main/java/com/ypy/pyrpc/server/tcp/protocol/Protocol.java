package com.ypy.pyrpc.server.tcp.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class Protocol<T> {
    @Data
    @Builder
    public static class Header {
        private byte magic;
        private byte version;
        private byte serializer;
        private byte type;
        private byte status;
        private long requestId;
        private int bodyLength;
    }

    private Header header;
    private T body; // body: RpcRequest or RpcResponse, will be serialized into bytes
}
