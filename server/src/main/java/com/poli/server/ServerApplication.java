package com.poli.server;

import com.poli.common.config.StaticConfigurations;
import com.poli.server.socket.SocketServer;

public class ServerApplication {
    public static void main(String[] args) {

        SocketServer server = new SocketServer(StaticConfigurations.SERVER_SOCKET_PORT);

        server.start();
    }
}
