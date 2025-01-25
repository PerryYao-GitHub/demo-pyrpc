package com.ypy.pyrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.app.RpcConstant;
import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.spi.registry.Registry;
import com.ypy.pyrpc.spi.registry.RegistryFactory;
import com.ypy.pyrpc.spi.serializer.Serializer;
import com.ypy.pyrpc.spi.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());

        // todo: service version
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(ServiceMetaInfo.serviceKey(serviceName, RpcConstant.DEFAULT_SERVICE_VERSION));

        ServiceMetaInfo serviceMetaInfo = serviceMetaInfoList.get(0);


        // http
        Serializer serializer = SerializerFactory.getInstance(rpcConfig.getSerializer());
        byte[] bodyBytes = serializer.serialize(rpcRequest);
        try (HttpResponse httpResponse = HttpRequest
                .post(serviceMetaInfo.getServiceAddr())
                .body(bodyBytes).execute()
        ) {
            byte[] resultBytes = httpResponse.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(resultBytes, RpcResponse.class);
            return rpcResponse.getData();
        }
    }
}
