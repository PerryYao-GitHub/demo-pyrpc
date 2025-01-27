package com.ypy.pyrpc.proxy;

import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.app.RpcConstant;
import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.server.RpcClient;
import com.ypy.pyrpc.server.RpcClientFactory;
import com.ypy.pyrpc.spi.loadbalancer.Loadbalancer;
import com.ypy.pyrpc.spi.loadbalancer.LoadbalancerFactory;
import com.ypy.pyrpc.spi.registry.Registry;
import com.ypy.pyrpc.spi.registry.RegistryFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

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

        String serviceNameVer = ServiceMetaInfo.serviceNameVer(serviceName, RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceNameVer);

        Loadbalancer loadbalancer = LoadbalancerFactory.getInstance(RpcApplication.getRpcConfig().getLoadbalancer());
        Map<String, Object> context = Map.of("serviceNameVer", serviceNameVer); // todo
        ServiceMetaInfo serviceMetaInfo = loadbalancer.select(serviceMetaInfoList, context);

        RpcClient rpcClient = RpcClientFactory.getInstance(RpcApplication.getRpcConfig().getServerType());
        RpcResponse rpcResponse = rpcClient.doRequest(rpcRequest, serviceMetaInfo);
        return rpcResponse.getData();
    }
}
