package com.ypy.pyrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.server.tcp.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TcpClient {
    public static RpcResponse doRequest(
            RpcRequest rpcRequest,
            ServiceMetaInfo serviceMetaInfo
    ) throws InterruptedException, ExecutionException {
        Vertx vertx = Vertx.vertx();
        CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();

        NetClient netClient = vertx.createNetClient();
        netClient.connect(
                serviceMetaInfo.getServicePost(),
                serviceMetaInfo.getServiceHost(),
                netSocketAsyncResult -> {
                    if (!netSocketAsyncResult.succeeded()) {
                        System.err.println(netSocketAsyncResult.cause().getMessage());
                        return;
                    }

                    System.out.println("TCP connection established");
                    NetSocket netSocket = netSocketAsyncResult.result();

                    // make date
                    Protocol.Header header = Protocol.Header.builder()
                            .magic(ProtocolConst.MAGIC)
                            .version(ProtocolConst.VERSION)
                            .serializer(ProtocolSerializerEnum.getByKey(RpcApplication.getRpcConfig().getSerializer()).getCode())
                            .type(ProtocolTypeEnum.REQUEST.getCode())
                            .requestId(IdUtil.getSnowflakeNextId())
                            .build();
                    Protocol<RpcRequest> rpcRequestProtocol = new Protocol<>(header, rpcRequest);
                    rpcRequestProtocol.setHeader(header);
                    rpcRequestProtocol.setBody(rpcRequest);

                    // send data
                    try {
                        Buffer buffer = ProtocolUtils.encode(rpcRequestProtocol);
                        netSocket.write(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException("Encode failed", e);
                    }

                    // get response data
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                        try {
                            Protocol<RpcResponse> responseProtocol =
                                    (Protocol<RpcResponse>) ProtocolUtils.decode(buffer);
                            responseCompletableFuture.complete(responseProtocol.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("Decode failed", e);
                        }
                    });
                    netSocket.handler(bufferHandlerWrapper);
                }
        );

        RpcResponse rpcResponse = responseCompletableFuture.get();
        // close connect
        netClient.close();
        return rpcResponse;
    }
}
