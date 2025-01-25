package com.ypy.pyrpc.spi.registry.impl;

import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.spi.registry.Registry;

import java.util.List;

public class NoRegistry implements Registry {
    @Override
    public void init(RpcConfig.RegistryConfig registryConfig) {}

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {}

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {}

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String[] serviceNameAndVersion = serviceKey.split(":");
        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName(serviceNameAndVersion[0])
                .serviceVersion(serviceNameAndVersion[1])
                .serviceHost(RpcApplication.getRpcConfig().getServerHost())
                .servicePost(RpcApplication.getRpcConfig().getServerPort())
                .build();
        return List.of(serviceMetaInfo);
    }

    @Override
    public void destroy() {}

    @Override
    public void heartBeat() {}

    @Override
    public void watch(String serviceNodeKey) {}
}
