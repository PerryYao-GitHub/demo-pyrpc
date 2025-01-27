package com.ypy.pyrpc.spi.loadbalancer.impl;

import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.spi.loadbalancer.Loadbalancer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadbalancer implements Loadbalancer {
    private final AtomicInteger idx = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, Map<String, Object> context) {
        int sz = serviceMetaInfoList.size();
        if (sz == 0) return null;
        if (sz == 1) return serviceMetaInfoList.get(0);
        int i = idx.getAndIncrement() % sz;
        return serviceMetaInfoList.get(i);
    }
}
