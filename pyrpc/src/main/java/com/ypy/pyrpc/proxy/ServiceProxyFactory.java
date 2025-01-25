package com.ypy.pyrpc.proxy;

import com.ypy.pyrpc.app.RpcApplication;

import java.lang.reflect.Proxy;

public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceInterface) {
        if (RpcApplication.getRpcConfig().isMock()) return getMockProxy(serviceInterface);

        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                new ServiceProxy()
        );
    }

    public static <T> T getMockProxy(Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                new MockServiceProxy()
        );
    }
}
