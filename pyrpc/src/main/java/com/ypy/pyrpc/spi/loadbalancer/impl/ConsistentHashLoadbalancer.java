package com.ypy.pyrpc.spi.loadbalancer.impl;

import com.ypy.pyrpc.model.ServiceMetaInfo;
import com.ypy.pyrpc.spi.loadbalancer.Loadbalancer;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadbalancer implements Loadbalancer {
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();
    private static final int VIRTUAL_NODE_CNT = 12;
    private int getHash(Object o) { return o.hashCode(); }

    @Override
    public ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, Map<String, Object> context) {
        if (serviceMetaInfoList.isEmpty()) return null;
        if (serviceMetaInfoList.size() == 1) return serviceMetaInfoList.get(0);

        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_CNT; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddr() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        int hash = getHash(context);
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) entry = virtualNodes.firstEntry();
        return entry.getValue();
    }
}
