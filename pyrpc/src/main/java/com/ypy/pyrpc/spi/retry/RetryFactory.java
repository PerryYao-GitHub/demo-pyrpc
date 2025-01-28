package com.ypy.pyrpc.spi.retry;

import com.ypy.pyrpc.spi.SpiLoader;

public class RetryFactory {
    static { SpiLoader.load(Retry.class); }
    static public Retry getInstance(String implClassKey) { return SpiLoader.getInstance(Retry.class, implClassKey); }
}
