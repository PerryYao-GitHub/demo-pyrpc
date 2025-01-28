package com.ypy.pyrpc.spi.retry.impl;

import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.spi.retry.Retry;

import java.util.concurrent.Callable;

public class NoRetry implements Retry {
    @Override
    public RpcResponse retry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
