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

    /**
     * ServiceNameVersion: com....UserService:1.0.0
     * @return
     */
    public String getServiceNameVer() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    public static String serviceNameVer(String serviceName, String serviceVersion) { return String.format("%s:%s", serviceName, serviceVersion); }

    /**
     * ServiceNameVersionAddress: com....UserService:1.0.0/http://127.0.0.1:8080
     * @return
     */
    public String getServiceNameVerAddr() {
        return String.format("%s/%s:%s", getServiceNameVer(), serviceHost, servicePost);
    }

    /**
     * ServiceAddress: http://127.0.0.1:8080
     * @return
     */
    public String getServiceAddr() {
        String addr = String.format("%s:%s", serviceHost, servicePost);
        if (!StrUtil.contains(addr, "http")) addr = "http://" + addr;
        return addr;
    }
}
