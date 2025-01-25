package com.ypy.pyrpc.app;

import com.ypy.pyrpc.config.RpcConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("RpcApplication initialized with config: {}", rpcConfig);
    }

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = RpcConfig.loadConfig(RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            log.warn("Failed to load configuration file. Using default config.", e);
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig() { return rpcConfig; }
}
