package com.ypy.pyrpc.spi.loadbalancer.impl;

import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.spi.loadbalancer.Loadbalancer;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadbalancer implements Loadbalancer {
    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, Map<String, Object> context) {
        int sz = serviceMetaInfoList.size();
        if (sz == 0) return null;
        if (sz == 1) return serviceMetaInfoList.get(0);
        return serviceMetaInfoList.get(random.nextInt(sz));
    }
}
