package com.ypy.pyrpc.spi.retry.impl;

import com.github.rholder.retry.*;
import com.ypy.pyrpc.model.RpcResponse;
import com.ypy.pyrpc.spi.retry.Retry;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixedIntervalRetry implements Retry {
    @Override
    public RpcResponse retry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("Retry attempt {}", attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
