package com.ypy.pyrpc.server.tcp;

import com.ypy.pyrpc.app.RpcLocalRegistry;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.server.tcp.protocol.Protocol;
import com.ypy.pyrpc.server.tcp.protocol.ProtocolTypeEnum;
import com.ypy.pyrpc.server.tcp.protocol.ProtocolUtils;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            Protocol<RpcRequest> protocol;
            try {
                protocol = (Protocol<RpcRequest>) ProtocolUtils.decode(buffer);
            } catch (Exception e) {
                throw new RuntimeException("Decode Err", e);
            }
            RpcRequest rpcRequest = protocol.getBody();
            RpcResponse rpcResponse = new RpcResponse();
            try {
                Class<?> serviceImpl = RpcLocalRegistry.getServiceImpl(rpcRequest.getServiceName());
                Method method = serviceImpl.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(serviceImpl.newInstance(), rpcRequest.getArgs());

                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMsg("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMsg(e.getMessage());
                rpcResponse.setException(e);
            }

            Protocol.Header header = protocol.getHeader();
            header.setType(ProtocolTypeEnum.RESPONSE.getCode());
            Protocol<RpcResponse> responseProtocol = new Protocol<>(header, rpcResponse);
            try {
                Buffer encodeBuffer = ProtocolUtils.encode(responseProtocol);
                netSocket.write(encodeBuffer);
            } catch (IOException e) {
                throw new RuntimeException("Encode Err", e);
            }
        });
        netSocket.handler(bufferHandlerWrapper);
    }
}
