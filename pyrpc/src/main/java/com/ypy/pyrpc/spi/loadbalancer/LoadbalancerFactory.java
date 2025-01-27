package com.ypy.pyrpc.spi.loadbalancer;

import com.ypy.pyrpc.spi.SpiLoader;

public class LoadbalancerFactory {
    static { SpiLoader.load(Loadbalancer.class); }
    public static Loadbalancer getInstance(String implClassKey) { return SpiLoader.getInstance(Loadbalancer.class, implClassKey); }
}
