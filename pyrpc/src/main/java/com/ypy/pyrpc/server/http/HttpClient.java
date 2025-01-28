package com.ypy.pyrpc.server.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.server.RpcClient;
import com.ypy.pyrpc.spi.serializer.Serializer;
import com.ypy.pyrpc.spi.serializer.SerializerFactory;

public class HttpClient implements RpcClient {
    @Override
    public RpcResponse request(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception {
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        byte[] bodyBytes = null;
        bodyBytes = serializer.serialize(rpcRequest);
        try (HttpResponse httpResponse = HttpRequest
                .post(serviceMetaInfo.getServiceAddr())
                .body(bodyBytes).execute()
        ) {
            byte[] resultBytes = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            return rpcResponse;
        }
    }
}
