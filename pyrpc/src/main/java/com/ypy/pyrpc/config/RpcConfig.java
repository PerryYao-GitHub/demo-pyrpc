package com.ypy.pyrpc.config;

import com.ypy.pyrpc.server.RpcServerTypeKeys;
import com.ypy.pyrpc.spi.loadbalancer.LoadbalancerKeys;
import com.ypy.pyrpc.spi.registry.RegistryKeys;
import com.ypy.pyrpc.spi.serializer.SerializerKeys;
import com.ypy.pyrpc.spi.tolerance.ToleranceKeys;
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
    private String serverType = RpcServerTypeKeys.TCP;
    private boolean mock = false;
    private RegistryConfig registryConfig = new RegistryConfig();
    private String serializer = SerializerKeys.JDK; // serializerKey
    private String loadbalancer = LoadbalancerKeys.ROUND_ROBIN;
    private String retry = RegistryKeys.NO;
    private String tolerance = ToleranceKeys.FAIL_FAST;
}
