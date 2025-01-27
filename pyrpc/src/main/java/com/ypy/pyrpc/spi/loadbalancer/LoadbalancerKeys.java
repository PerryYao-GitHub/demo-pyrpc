package com.ypy.pyrpc.spi.loadbalancer;

public interface LoadbalancerKeys {
    String ROUND_ROBIN = "round-robin";
    String RANDOM = "random";
    String CONSISTENT_HASH = "consistent-hash";
}
