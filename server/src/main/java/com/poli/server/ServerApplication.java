package com.poli.server;

import com.poli.server.socket.SocketServer;

public class ServerApplication {
    public static void main(String[] args) {

        SocketServer server = new SocketServer(5000);

        server.start();
    }
}
