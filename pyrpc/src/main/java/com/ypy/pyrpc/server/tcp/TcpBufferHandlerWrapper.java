package com.ypy.pyrpc.server.tcp;

import com.ypy.pyrpc.server.tcp.protocol.ProtocolConst;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        RecordParser parser = RecordParser.newFixed(ProtocolConst.HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            int bodyLength = -1;
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (-1 == bodyLength) {
                    // 1. 首先解析 Header（固定长度 17 字节）
                    bodyLength = buffer.getInt(13); // 获取 Body 的长度，假设Header的索引 13..=16 字节表示 BodyLength
                    parser.fixedSizeMode(bodyLength); // 根据Body的长度设置接下来的解析模式
                    resultBuffer.appendBuffer(buffer); // 将Header数据缓存起来
                } else {
                    // 2. 接收 Body 数据
                    resultBuffer.appendBuffer(buffer); // 将接收到的Body数据添加到缓存中

                    if (resultBuffer.length() >= bodyLength + ProtocolConst.HEADER_LENGTH) {
                        bufferHandler.handle(resultBuffer); // 调用外部传入的回调，处理完整的消息
                        // 4. 处理完后，重置状态，准备处理下一个数据包
                        parser.fixedSizeMode(ProtocolConst.HEADER_LENGTH);
                        bodyLength = -1; // 重置Body长度
                        resultBuffer = Buffer.buffer(); // 重置缓存
                    }
                }
            }
        });
        return parser;
    }

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}
