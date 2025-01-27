package com.ypy.pyrpc.spi.loadbalancer;

import com.ypy.pyrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface Loadbalancer {
    /**
     *
     * @param serviceMetaInfoList
     * @param context: Provide additional contextual information for load balancing algorithms to more intelligently select service instances.
     * @return
     */
    ServiceMetaInfo select(List<ServiceMetaInfo> serviceMetaInfoList, Map<String, Object> context);
}
