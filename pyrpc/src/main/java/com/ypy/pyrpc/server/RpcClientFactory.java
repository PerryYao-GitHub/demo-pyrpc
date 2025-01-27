package com.ypy.pyrpc.server;

import com.ypy.pyrpc.server.http.HttpClient;
import com.ypy.pyrpc.server.tcp.TcpClient;

import java.util.HashMap;
import java.util.Map;

public class RpcClientFactory {
    private static final Map<String, RpcClient> clientMap = new HashMap<>();

    static {
        clientMap.put(RpcServerTypeKeys.TCP, new TcpClient());
        clientMap.put(RpcServerTypeKeys.HTTP, new HttpClient());
    }

    public static RpcClient getInstance(String implClassKey) { return clientMap.get(implClassKey); }
}
