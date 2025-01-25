package com.ypy.pyrpc.model;

import cn.hutool.core.util.StrUtil;
import com.ypy.pyrpc.app.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMetaInfo {
    private String serviceName;
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    private String serviceHost;
    private int servicePost;

    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    public static String serviceKey(String serviceName, String serviceVersion) { return String.format("%s:%s", serviceName, serviceVersion); }

    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePost);
    }

    public String getServiceAddr() {
        String addr = String.format("%s:%s", serviceHost, servicePost);
        if (!StrUtil.contains(addr, "http")) addr = "http://" + addr;
        return addr;
    }
}
