package com.ypy.pyrpc.spi.tolerance;

import com.ypy.pyrpc.spi.SpiLoader;

public class ToleranceFactory {
    static { SpiLoader.load(Tolerance.class); }
    public static Tolerance getInstance(String implClassKey) { return SpiLoader.getInstance(Tolerance.class, implClassKey); }
}
