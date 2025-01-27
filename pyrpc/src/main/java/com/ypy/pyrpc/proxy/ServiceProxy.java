package com.ypy.pyrpc.proxy;

import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.app.RpcConstant;
import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.server.tcp.TcpClient;
import com.ypy.pyrpc.spi.registry.Registry;
import com.ypy.pyrpc.spi.registry.RegistryFactory;

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
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(ServiceMetaInfo.serviceNameVer(serviceName, RpcConstant.DEFAULT_SERVICE_VERSION));

        ServiceMetaInfo serviceMetaInfo = serviceMetaInfoList.get(0);

        // todo
        // http
        // RpcResponse rpcResponse = HttpClient.doRequest(rpcRequest, serviceMetaInfo);

        // tcp
        RpcResponse rpcResponse = TcpClient.doRequest(rpcRequest, serviceMetaInfo);
        return rpcResponse.getData();
    }
}
