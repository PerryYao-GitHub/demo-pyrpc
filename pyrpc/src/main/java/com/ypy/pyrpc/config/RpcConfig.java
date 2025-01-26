package com.ypy.pyrpc.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.ypy.pyrpc.spi.registry.RegistryKeys;
import com.ypy.pyrpc.spi.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    @Data
    public static class RegistryConfig {
        private String registry = RegistryKeys.ETCD; // registryKey
        private String addr = "http://127.0.0.1:2375";
        private String username;
        private String password;
        private Long timeout = 10000L;
    }

    private String serverHost = "127.0.0.1";
    private int serverPort = 8080;
    private boolean mock = false;
    private String serializer = SerializerKeys.JDK; // serializerKey
    private RegistryConfig registryConfig = new RegistryConfig();
}
