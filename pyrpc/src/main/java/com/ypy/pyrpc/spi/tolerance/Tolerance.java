package com.ypy.pyrpc.spi.tolerance;

import com.ypy.pyrpc.model.RpcResponse;

import java.util.Map;

public interface Tolerance {
    RpcResponse tolerate(Exception e, Map<String, Object> context);
}
