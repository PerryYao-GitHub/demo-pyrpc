package com.ypy.pyrpc.spi.registry;

import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    void init(RpcConfig.RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * @param serviceKey: "serviceName:serviceVersion"
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    void destroy();

    void heartBeat();

    void watch(String serviceNodeKey);
}
