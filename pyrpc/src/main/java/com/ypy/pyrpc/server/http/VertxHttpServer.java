package com.ypy.pyrpc.server.http;

import com.ypy.pyrpc.server.RpcServer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class VertxHttpServer implements RpcServer {
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(new HttpServerHandler());

        httpServer.listen(port, result -> {
            if (result.succeeded()) System.out.println("Server is now listening on port " + port);
            else System.out.println("Failed to start server: " + result.cause());
        });
    }
}
