package com.ypy.pyrpc.server.tcp.protocol;

import com.ypy.pyrpc.spi.serializer.SerializerKeys;
import lombok.Getter;

@Getter
public enum ProtocolSerializerEnum {
    JDK((byte) 0, SerializerKeys.JDK),
    JSON((byte) 1, SerializerKeys.JSON),
    KRYO((byte) 2, SerializerKeys.KRYO);

    private final byte code;
    private final  String key;

    ProtocolSerializerEnum(byte code, String key) {
        this.code = code;
        this.key = key;
    }

    public static ProtocolSerializerEnum getByCode(byte code) {
        for (ProtocolSerializerEnum e : ProtocolSerializerEnum.values()) {
            if (e.code == code) return e;
        }
        return null;
    }

    public static ProtocolSerializerEnum getByKey(String key) {
        for (ProtocolSerializerEnum e : ProtocolSerializerEnum.values()) {
            if (e.key.equals(key)) return e;
        }
        return null;
    }
}
