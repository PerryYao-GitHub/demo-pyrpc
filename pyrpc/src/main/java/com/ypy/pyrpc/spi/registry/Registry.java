package com.ypy.pyrpc.spi.registry;

import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {
    /**
     * initialize registry for both PROVIDER and CONSUMER
     * @param registryConfig
     */
    void init(RpcConfig.RegistryConfig registryConfig);

    /**
     * help PROVIDER to register their service (info) into registry
     * and these services should also be registered and managed by LocalRegistry
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * help PROVIDER to unregister service
     * @param serviceMetaInfo
     * @throws Exception
     */
    void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * help CONSUMER to search a group of service meta info by ServiceNameVersion
     * @param serviceNameVer: "serviceName:serviceVersion"
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceNameVer);

    /**
     * when a PROVIDER offline, clear correlative service info it provided in registry
     */
    void destroy();

    /**
     * send update message to registry, confirming PROVIDED alive
     */
    void heartBeat();

    /**
     * help CONSUMER to watch PROVIDER, when a Provider offline (actively or passively), it will update local cache info of CONSUMER
     * @param fullRegistryServiceKey: /[RPC_ROOT_PATH]/[ServiceName]:[ServiceVersion]/[ServiceAddr]
     */
    void watch(String fullRegistryServiceKey, String serviceNameVer);
}
