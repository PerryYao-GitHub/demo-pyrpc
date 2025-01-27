package com.ypy.pyrpc.server.tcp.protocol;

import lombok.Getter;

@Getter
public enum ProtocolStatusEnum {
    OK((byte) 20, "ok"),
    BAD_REQUEST((byte) 40, "bad request"),
    BAD_RESPONSE((byte) 50, "bad response");

    private final byte code;
    private final String text;

    ProtocolStatusEnum(byte code, String text) {
        this.code = code;
        this.text = text;
    }
}
