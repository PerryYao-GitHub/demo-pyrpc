package com.ypy.pyrpc.server;

import com.ypy.pyrpc.model.RpcRequest;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.model.ServiceMetaInfo;

public interface RpcClient {
    RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception;
}
