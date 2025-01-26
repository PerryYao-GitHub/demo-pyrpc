package com.ypy.pyrpc.app;

import com.ypy.pyrpc.config.RpcConfig;
import com.ypy.pyrpc.config.RpcConfigReader;
import com.ypy.pyrpc.spi.registry.Registry;
import com.ypy.pyrpc.spi.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * init RpcConfig
     * and Registry
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        // config
        rpcConfig = newRpcConfig;
        log.info("RpcApplication initialized with config: {}", rpcConfig);

        // registry
        RpcConfig.RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("Registry initialized with config: {}", registryConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = RpcConfigReader.loadConfig(RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            log.warn("Failed to load configuration file. Using default config.", e);
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) init();
            }
        }
        return rpcConfig;
    }
}
