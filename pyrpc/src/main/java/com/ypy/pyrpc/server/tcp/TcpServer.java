package com.ypy.pyrpc.server.tcp;

import com.ypy.pyrpc.server.RpcServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

public class TcpServer implements RpcServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        NetServer server = vertx.createNetServer();
        server.connectHandler(new TcpServerHandler());
        server.listen(port, netServerAsyncResult -> {
            if (netServerAsyncResult.succeeded()) System.out.println("TcpServer started on port " + port);
            else System.out.println("TcpServer failed to start on port " + port);
        });
    }
}
