package com.ypy.pyrpc.spi.serializer;

import com.ypy.pyrpc.spi.SpiLoader;

public class SerializerFactory {
    static { SpiLoader.load(Serializer.class); }
    public static Serializer getInstance(String implClassKey) { return SpiLoader.getInstance(Serializer.class, implClassKey); }
}
