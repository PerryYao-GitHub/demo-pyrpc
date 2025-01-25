package com.ypy.pyrpc.spi.registry;

import com.ypy.pyrpc.spi.SpiLoader;

public class RegistryFactory {
    static { SpiLoader.load(Registry.class); }
    public static Registry getInstance(String implClassKey) { return SpiLoader.getInstance(Registry.class, implClassKey); }
}
