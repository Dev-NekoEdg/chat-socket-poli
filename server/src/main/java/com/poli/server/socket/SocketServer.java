package com.poli.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

    private final int port;
    private final ExecutorService executor;

    public SocketServer(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("================================");
            System.out.println(" CHAT SOCKET POLI - SERVER");
            System.out.println("================================");
            System.out.println("Servidor iniciado en puerto: " + port);
            System.out.println("Esperando clientes...");

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println(
                    "Nuevo cliente conectado: " +
                    clientSocket.getRemoteSocketAddress()
                );

                executor.submit(
                    new ClientHandler(clientSocket)
                );
            }

        } catch (IOException e) {

            System.err.println(
                "Error iniciando servidor: " + e.getMessage()
            );

        } finally {
            executor.shutdown();
        }
    }
}