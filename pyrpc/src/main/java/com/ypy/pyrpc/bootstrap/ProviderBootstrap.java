package com.ypy.pyrpc.bootstrap;

import com.ypy.pyrpc.app.LocalRegistry;
import com.ypy.pyrpc.app.RpcApplication;
import com.ypy.pyrpc.app.RpcConstant;
import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.model.ServiceRegisterInfo;
import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.server.RpcServer;
import com.ypy.pyrpc.server.http.VertxHttpServer;
import com.ypy.pyrpc.spi.registry.Registry;
import com.ypy.pyrpc.spi.registry.RegistryFactory;

import java.util.List;

public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo> serviceRegisterInfoList) {
        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            LocalRegistry.register(serviceRegisterInfo.getServiceName(), serviceRegisterInfo.getImplClass());

            RpcConfig.RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                    .serviceName(serviceRegisterInfo.getServiceName())
                    .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION) // todo
                    .serviceHost(rpcConfig.getServerHost())
                    .servicePost(rpcConfig.getServerPort())
                    .build();

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceRegisterInfo.getServiceName() + " register failed", e);
            }
        }

        RpcServer rpcServer = new VertxHttpServer();
        rpcServer.doStart(rpcConfig.getServerPort());
    }
}
