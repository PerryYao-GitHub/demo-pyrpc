package com.ypy.pyrpc.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

public class RpcConfigReader {
    public static RpcConfig loadConfig(String prefix, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)) configFileBuilder.append("-").append(env);
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());

        RpcConfig rpcConfig = props.toBean(RpcConfig.class, prefix);
        RpcConfig.RegistryConfig registryConfig = props.toBean(RpcConfig.RegistryConfig.class, prefix + ".registry");
        rpcConfig.setRegistryConfig(registryConfig);
        return rpcConfig;
    }

    public static RpcConfig loadConfig(String prefix) { return loadConfig(prefix, ""); }
}
