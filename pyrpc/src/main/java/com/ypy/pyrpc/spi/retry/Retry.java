package com.ypy.pyrpc.spi.retry;

import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;

import java.util.concurrent.Callable;

public interface Retry {
    RpcResponse retry(Callable<RpcResponse> callable) throws Exception;
}
