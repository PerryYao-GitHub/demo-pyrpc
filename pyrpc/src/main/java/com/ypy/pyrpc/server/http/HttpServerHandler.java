package com.ypy.pyrpc.server.http;

import com.ypy.pyrpc.app.RpcLocalRegistry;
import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.spi.serializer.Serializer;
import com.ypy.pyrpc.spi.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null) {
                rpcResponse.setMsg("RPC request is null");
            } else {
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
            }
            doResponse(request, rpcResponse, serializer);
        });
    }

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request
                .response()
                .putHeader("content-type", "application/json");

        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(serialized));
        } catch (Exception e) {
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }
}
