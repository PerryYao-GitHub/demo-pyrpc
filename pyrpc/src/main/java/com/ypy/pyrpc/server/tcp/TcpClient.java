package com.ypy.pyrpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.server.RpcClient;
import com.ypy.pyrpc.server.tcp.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TcpClient implements RpcClient {
    private static Vertx vertx = Vertx.vertx(); // signal instance

    private static NetClient netClient = vertx.createNetClient(new NetClientOptions()); // signal instance

    @Override
    public RpcResponse request(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();

        netClient.connect(
                serviceMetaInfo.getServicePost(),
                serviceMetaInfo.getServiceHost(),
                netSocketAsyncResult -> {
                    if (!netSocketAsyncResult.succeeded()) {
                        System.err.println(netSocketAsyncResult.cause().getMessage());
                        return;
                    }
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
        return rpcResponse;
    }
}
