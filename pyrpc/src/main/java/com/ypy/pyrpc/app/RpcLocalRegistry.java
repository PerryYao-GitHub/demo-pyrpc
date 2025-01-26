package com.ypy.pyrpc.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcLocalRegistry {
    private static final Map<String, Class<?>> registeredServiceImpl = new ConcurrentHashMap<String, Class<?>>();

    public static void register(String serviceName, Class<?> serviceImpl) {
        registeredServiceImpl.put(serviceName, serviceImpl);
    }

    public static Class<?> getServiceImpl(String serviceName) {
        return registeredServiceImpl.get(serviceName);
    }

    public static void removeService(String serviceName) {
        registeredServiceImpl.remove(serviceName);
    }
}
