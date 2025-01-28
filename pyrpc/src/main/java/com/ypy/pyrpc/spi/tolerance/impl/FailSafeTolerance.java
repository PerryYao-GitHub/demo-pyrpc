package com.ypy.pyrpc.spi.tolerance.impl;

import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.spi.tolerance.Tolerance;

import java.util.Map;

public class FailSafeTolerance implements Tolerance {
    @Override
    public RpcResponse tolerate(Exception e, Map<String, Object> context) {
        throw new RuntimeException("FailSafeTolerance: Can't Find Service", e);
    }
}
