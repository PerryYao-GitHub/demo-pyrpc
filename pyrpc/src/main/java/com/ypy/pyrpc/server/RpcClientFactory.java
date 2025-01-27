package com.ypy.pyrpc.server;

import com.ypy.pyrpc.server.tcp.TcpClient;

import java.util.HashMap;
import java.util.Map;

public class RpcClientFactory {
    private static final Map<String, RpcClient> clientMap = new HashMap<>();

    static {
        clientMap.put(RpcServerTypeKeys.TCP, new TcpClient());
        clientMap.put(RpcServerTypeKeys.HTTP, null);
    }

    public static RpcClient getInstance(String implClassKey) { return clientMap.get(implClassKey); }
}
