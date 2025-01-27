package com.ypy.pyrpc.server.tcp.protocol;

import lombok.Getter;

@Getter
public enum ProtocolTypeEnum {
    REQUEST((byte) 0),
    RESPONSE((byte) 1),
    HEART_BEAT((byte) 2),
    OTHERS((byte) 3);

    private final byte code;

    ProtocolTypeEnum(byte code) {
        this.code = code;
    }

    public static ProtocolTypeEnum getByCode(byte code) {
        for (ProtocolTypeEnum e : ProtocolTypeEnum.values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
