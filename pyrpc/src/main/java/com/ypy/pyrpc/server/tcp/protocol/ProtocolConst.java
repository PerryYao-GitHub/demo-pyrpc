package com.ypy.pyrpc.server.tcp.protocol;

public interface ProtocolConst {
    int HEADER_LENGTH = 17;
    byte MAGIC = (byte) 0x89;
    byte VERSION = (byte) 0x01;
}
