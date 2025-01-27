package com.ypy.pyrpc.server.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.spi.serializer.Serializer;
import com.ypy.pyrpc.spi.serializer.SerializerFactory;

import java.io.IOException;

@Deprecated
public class HttpClientUtil {
    public static RpcResponse doRequest(
            RpcRequest rpcRequest,
            ServiceMetaInfo serviceMetaInfo
    ) throws IOException {
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
