package com.ypy.pyrpc.server;

import com.ypy.pyrpc.server.http.HttpServer;
import com.ypy.pyrpc.server.tcp.TcpServer;

import java.util.HashMap;
import java.util.Map;

public class RpcServerFactory {
    private static Map<String, RpcServer> serverMap = new HashMap<String, RpcServer>();

    static {
        serverMap.put(RpcServerTypeKeys.HTTP, new HttpServer());
        serverMap.put(RpcServerTypeKeys.TCP, new TcpServer());
    }

    public static RpcServer getInstance(String implClassKey) { return serverMap.get(implClassKey); }
}
